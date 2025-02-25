using Microsoft.AspNetCore.Mvc;

namespace KeycloakAuthorization.Controllers;

[ApiController]
[Route("[controller]")]
public class TokenController : ControllerBase
{
    readonly HttpClient _httpClient;
    readonly Dictionary<string, string> _formDataValues;
    public TokenController(IConfiguration configuration, IHttpClientFactory httpClientFactory)
    {
        _httpClient = httpClientFactory.CreateClient("keycloak");
        _formDataValues = new Dictionary<string, string>
        {
            { "grant_type", "password" },
            { "client_id", configuration["Keycloak:resource"] },
            { "client_secret", configuration["Keycloak:credentials:secret"] }
        };
    }
    [HttpGet]
    public async Task<IActionResult> Get(string username, string password)
    {
        _formDataValues.Add("username", username);
        _formDataValues.Add("password", password);
        var formData = new FormUrlEncodedContent(_formDataValues);
        var response = await _httpClient.PostAsync("http://keycloak:8080/realms/bem/protocol/openid-connect/token", formData);

        if (response.IsSuccessStatusCode)
        {
            var content = await response.Content.ReadFromJsonAsync<Jwt>();
            return Ok(content.access_token);
        }
        return BadRequest();
    }

    public class Jwt
    {
        public string access_token { get; set; }
    }
}
