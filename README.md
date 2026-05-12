# WeatherApp — Приложение погоды в реальном времени

Кроссплатформенное приложение на Compose Multiplatform — просмотр погоды с поддержкой Android, iOS, Linux и Web.

## Основные возможности

- Поиск и добавление городов
- Отображение текущей температуры, влажности, ветра, давления
- Прогноз погоды на 5 дней
- Кеширование данных для офлайн-просмотра
- Адаптивный UI под особенности каждой платформы
- Поддержка светлой и тёмной темы
- Обработка исключительных ситуаций с выводом сообщений

## Технологии

| Компонент              | Технология                              |
|------------------------|-----------------------------------------|
| Фреймворк              | Compose Multiplatform 1.10.3            |
| Язык                   | Kotlin 2.3.21                           |
| HTTP-клиент            | Ktor Client 3.0.3                       |
| Сериализация           | kotlinx.serialization 1.7.3             |
| Асинхронность          | Kotlin Coroutines 1.10.2                |
| Архитектура            | MVVM (ViewModel + StateFlow)            |
| UI                     | Material 3 (Compose)                    |
| Кеширование            | In-memory cache (CacheManager)          |
| API                    | OpenWeatherMap API                      |
| Тесты                  | kotlin.test + kotlinx.coroutines.test   |
| CI/CD                  | GitHub Actions                          |

## Установка и запуск

```bash
git clone https://github.com/Xeiniya/WeatherApp.git
cd WeatherApp

# Android
./gradlew composeApp:assembleDebug

# Desktop
./gradlew composeApp:run

# Web
./gradlew composeApp:wasmJsBrowserRun
```

## Тестирование

```bash
# Все тесты
./gradlew composeApp:allTests

# Только модульные тесты
./gradlew composeApp:desktopTest
```

## Сборка под платформы

```bash
# Android APK
./gradlew composeApp:assembleDebug

# Desktop дистрибутив
./gradlew composeApp:createDistributable

# WebAssembly
./gradlew composeApp:wasmJsBrowserDistribution
```

## Структура проекта

```
composeApp/src/
├── commonMain/kotlin/com/example/weatherapp/
│   ├── model/              # WeatherModels.kt — модели данных
│   ├── network/            # ApiClient.kt — HTTP-клиент (Ktor)
│   ├── cache/              # CacheManager.kt — кеширование данных
│   ├── viewmodel/          # WeatherViewModel.kt — MVVM
│   └── ui/                 # App.kt, WeatherAppContent.kt — Compose UI
│       └── theme/          # Theme.kt — темы
├── androidMain/            # Android-специфичный код
├── iosMain/                # iOS-специфичный код
├── desktopMain/            # Desktop (JVM) код
├── wasmJsMain/             # Web (WebAssembly) код
└── commonTest/             # Общие тесты
```

## Конфигурация API

Замените `API_KEY` в файле:
```
composeApp/src/commonMain/kotlin/com/example/weatherapp/network/ApiClient.kt
```
Ключ можно получить на [openweathermap.org](https://openweathermap.org/api)

## Author

Ксения Николаева
