using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace KeycloakAuthorization.Controllers;

[ApiController]
[Route("[controller]")]
public class WeatherForecastController : ControllerBase
{
    [Authorize("my-workspace#read")]
    [HttpGet]
    public async Task<IActionResult> Get()
    {
        return Ok();
    }
}
