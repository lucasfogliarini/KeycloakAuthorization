FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build

WORKDIR /app
COPY . .

RUN dotnet restore
RUN dotnet build -c Release -o /app/build

FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS runtime

WORKDIR /app

COPY --from=build /app/build .

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["dotnet", "KeycloakAuthorization.dll"]