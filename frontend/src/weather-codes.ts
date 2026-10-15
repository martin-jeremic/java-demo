export function describeWeatherCode(code: number): string {
  if (code === 0) return "Clear sky";
  if (code <= 3) return "Partly cloudy";
  if (code === 45 || code === 48) return "Fog";
  if (code >= 51 && code <= 57) return "Drizzle";
  if (code >= 61 && code <= 67) return "Rain";
  if (code >= 71 && code <= 77) return "Snow";
  if (code >= 80 && code <= 82) return "Rain showers";
  if (code === 85 || code === 86) return "Snow showers";
  if (code === 95 || code === 96 || code === 99) return "Thunderstorm";
  return "Conditions unavailable";
}

export function weatherSymbol(code: number): string {
  if (code === 0) return "☀";
  if (code <= 3) return "⛅";
  if (code === 45 || code === 48) return "☁";
  if (code >= 71 && code <= 77) return "❄";
  if (code >= 95) return "⚡";
  if (code >= 51) return "☂";
  return "◌";
}
