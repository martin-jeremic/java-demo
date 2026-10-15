import { FormEvent, useState } from "react";
import { getForecast, searchLocations } from "./api";
import type { CityLocation, WeatherForecast } from "./types";
import { describeWeatherCode, weatherSymbol } from "./weather-codes";

function App() {
  const [query, setQuery] = useState("");
  const [locations, setLocations] = useState<CityLocation[]>([]);
  const [forecast, setForecast] = useState<WeatherForecast | null>(null);
  const [isSearching, setIsSearching] = useState(false);
  const [isLoadingForecast, setIsLoadingForecast] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const searchTerm = query.trim();
    if (!searchTerm) {
      setError("Enter a city to search.");
      return;
    }

    setError(null);
    setLocations([]);
    setForecast(null);
    setIsSearching(true);
    try {
      const results = await searchLocations(searchTerm);
      setLocations(results);
      if (results.length === 0) setError("No cities found. Try another search.");
    } catch (searchError) {
      setError(searchError instanceof Error ? searchError.message : "City search failed.");
    } finally {
      setIsSearching(false);
    }
  }

  async function selectLocation(location: CityLocation) {
    setError(null);
    setForecast(null);
    setIsLoadingForecast(true);
    try {
      setForecast(await getForecast(location));
    } catch (forecastError) {
      setError(forecastError instanceof Error ? forecastError.message : "Forecast could not be loaded.");
    } finally {
      setIsLoadingForecast(false);
    }
  }

  return (
    <main className="page-shell">
      <header className="topbar">
        <a className="brand" href="/" aria-label="Weather Atlas home">
          <span className="brand-mark" aria-hidden="true">W</span>
          <span>Weather Atlas</span>
        </a>
        <span className="topbar-note">A little clarity for the days ahead</span>
      </header>

      <section className="intro">
        <p className="eyebrow">YOUR DAILY OUTLOOK</p>
        <h1>Find your kind of weather.</h1>
        <p className="intro-copy">Search anywhere in the world for a fresh, five-day forecast.</p>
      </section>

      <section className="search-panel" aria-label="City search">
        <form className="search-form" onSubmit={handleSearch}>
          <label htmlFor="city-search">Where are you headed?</label>
          <div className="search-controls">
            <div className="input-wrap">
              <span aria-hidden="true">⌕</span>
              <input
                id="city-search"
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="Try Copenhagen or Kyoto"
                autoComplete="off"
              />
            </div>
            <button type="submit" disabled={isSearching}>
              {isSearching ? "Searching…" : "Search cities"}
            </button>
          </div>
        </form>

        {locations.length > 0 && (
          <div className="location-results" aria-label="Search results">
            {locations.map((location, index) => (
              <button
                className="location-option"
                key={`${location.id ?? location.name}-${location.latitude}-${index}`}
                onClick={() => selectLocation(location)}
                disabled={isLoadingForecast}
              >
                <span className="location-pin" aria-hidden="true">↗</span>
                <span>
                  <strong>{location.name}</strong>
                  <small>{[location.region, location.country].filter(Boolean).join(", ")}</small>
                </span>
                <span className="option-arrow" aria-hidden="true">→</span>
              </button>
            ))}
          </div>
        )}

        {error && <p className="message error-message" role="alert">{error}</p>}
        {isLoadingForecast && <p className="message" role="status">Gathering the latest forecast…</p>}
      </section>

      {forecast && (
        <section className="forecast" aria-label={`Forecast for ${forecast.location}`}>
          <div className="forecast-heading">
            <div>
              <p className="eyebrow">RIGHT NOW IN</p>
              <h2>{forecast.location}</h2>
              <p className="muted">{forecast.timezone.replaceAll("_", " ")}</p>
            </div>
            <div className="current-weather">
              <span className="weather-symbol" aria-hidden="true">
                {weatherSymbol(forecast.current.weatherCode)}
              </span>
              <div>
                <strong>{Math.round(forecast.current.temperature)}°</strong>
                <span>{describeWeatherCode(forecast.current.weatherCode)}</span>
              </div>
            </div>
          </div>

          <div className="weather-details">
            <div><span>Feels like</span><strong>{Math.round(forecast.current.feelsLike)}°</strong></div>
            <div><span>Humidity</span><strong>{forecast.current.humidity}%</strong></div>
            <div><span>Wind</span><strong>{Math.round(forecast.current.windSpeed)} km/h</strong></div>
            <div><span>Precipitation</span><strong>{forecast.current.precipitation} mm</strong></div>
          </div>

          <div className="daily-heading">
            <h3>Five-day outlook</h3>
            <span>Local time</span>
          </div>
          <div className="daily-list">
            {forecast.daily.map((day) => (
              <article className="daily-card" key={day.date}>
                <span className="daily-date">{new Date(`${day.date}T12:00:00`).toLocaleDateString("en", { weekday: "short" })}</span>
                <span className="daily-symbol" aria-hidden="true">{weatherSymbol(day.weatherCode)}</span>
                <span className="daily-description">{describeWeatherCode(day.weatherCode)}</span>
                <span className="daily-rain">{day.precipitationProbability}% rain</span>
                <span className="daily-temperatures">
                  <strong>{Math.round(day.high)}°</strong><span>{Math.round(day.low)}°</span>
                </span>
              </article>
            ))}
          </div>
        </section>
      )}

      <footer>
        Forecast data by <a href="https://open-meteo.com/" target="_blank" rel="noreferrer">Open-Meteo</a>
      </footer>
    </main>
  );
}

export default App;
