import type { CityLocation, WeatherForecast } from "./types";

async function getJson<T>(url: string): Promise<T> {
  const response = await fetch(url);
  if (!response.ok) {
    const body = (await response.json().catch(() => null)) as { message?: string } | null;
    throw new Error(body?.message ?? "Weather data could not be loaded. Please try again.");
  }
  return (await response.json()) as T;
}

export function searchLocations(query: string): Promise<CityLocation[]> {
  const params = new URLSearchParams({ query });
  return getJson<CityLocation[]>(`/api/weather/locations?${params}`);
}

export function getForecast(location: CityLocation): Promise<WeatherForecast> {
  const params = new URLSearchParams({
    latitude: String(location.latitude),
    longitude: String(location.longitude),
    name: [location.name, location.region, location.country].filter(Boolean).join(", "),
  });
  return getJson<WeatherForecast>(`/api/weather/forecast?${params}`);
}
