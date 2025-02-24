using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace KeycloakAuthorization.Controllers;

[ApiController]
[Route("[controller]")]
public class MyWorkspaceController : ControllerBase
{
    [Authorize("my-workspace#read")]
    [HttpGet]
    public IActionResult Get()
    {
        return Ok();
    }

    [Authorize("my-workspace#write")]
    [HttpDelete]
    public IActionResult Delete()
    {
        return Ok();
    }
}
