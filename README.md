# ☕ Brewkery — Native Android Coffee & Bakery Ordering App

**Brewkery** is a native Android application built in Kotlin & Jetpack Compose, powered by a single REST API. The app features artisanal coffee & fresh oven bakes ordering with real-time price calculations, interactive customizations, full cart management, and active order tracking.

---

## 📱 Features & Highlights

- **Screen 1 — Home / Menu**:
  - Store announcement banner displaying shop tagline, delivery time (`20 - 30 mins`), and delivery fee (`$2.50`).
  - Active Order Tracking banner pinned at the top whenever an active order is placed (`#BK-XXXX` status badge).
  - Horizontal category selector with item counts and icons (☕ Hot Coffee, 🧊 Cold Brews, 🥐 Artisan Bakery).
  - Search filtering across item titles, descriptions, and taglines.
  - Detailed product cards featuring thumbnail badges (`BESTSELLER`, `SEASONAL`, etc.), ratings (⭐), review counts, and base prices.

- **Screen 2 — Item Detail & Dynamic Customization**:
  - Hero image banner with rating, calories, preparation time, and ingredients chips.
  - Interactive Customizations:
    - **Sizes**: Tall (+$0.00), Grande (+$0.65), Venti (+$1.25)
    - **Milk Options**: Oat Milk (+$0.00), Roasted Almond Milk (+$0.50), Organic Whole Milk (+$0.00)
    - **Sweetness Levels**: 0% Unsweetened, 50% Mild, 100% Standard Sweet
  - Real-time total price recalculation: `Total = (Base Price + Size Extra + Milk Extra) × Quantity`
  - Sticky bottom bar with quantity stepper and instant feedback snackbar.

- **Screen 3 — Cart**:
  - Itemized cart list with customized selections summary, unit prices, quantity controls, and delete option.
  - Precise Bill Details:
    - Item Subtotal
    - Delivery Fee ($2.50)
    - Taxes (8% calculated dynamically)
    - Grand Total / To Pay
  - Empty cart state with quick navigation back to the menu.

- **Screen 4 — Order Status**:
  - Instant ticket generation (`#BK-XXXX`).
  - Order status pipeline (`PREPARING` ⏱️ → Out for Delivery 🛵 → Delivered 🎉).
  - Itemized order receipt breakdown.

---

## 🛠 Tech Stack & Architecture

- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Unidirectional Data Flow (UDF)
- **Networking**: Retrofit 2 + Gson Converter + OkHttp
- **Asynchronous/Flow**: Kotlin Coroutines + `StateFlow` + `collectAsStateWithLifecycle`
- **Image Loading**: Coil (`AsyncImage`)
- **Navigation**: Navigation Compose (`NavHost`, `composable`)
- **Unit Testing**: JUnit 4 + Kotlinx Coroutines Test

---

## 🤖 How I Worked with AI (AI Section)

### AI Tools Used
- **Gemini / Android Studio AI Agent**: Code generation, architecture orchestration, component design, and UI layout crafting.
- **Claude / ChatGPT**: Data model schema generation and unit test assertion strategy.

### Actual Prompts Used

1. **Prompt 1 (Data Modeling & Retrofit API Setup)**:
   > *"Parse the provided JSON endpoints `data.json` and `api/items/{id}.json` for the Brewkery app. Create clean Kotlin data classes with `@SerializedName` annotations for StoreMeta, Category, MenuItem, Customizations, SizeOption, and MilkOption. Implement RetrofitApiService with Coroutines."*

2. **Prompt 2 (Real-Time Price Recalculation & ViewModel State)**:
   > *"Design an ItemDetailViewModel that handles dynamic item customization options (`sizes`, `milk_options`, `sugar_levels`). Implement real-time price recalculation using the formula `(basePrice + size.extraPrice + milk.extraPrice) * quantity` and expose selected state as StateFlows to Jetpack Compose."*

3. **Prompt 3 (Cart & Bill Calculation Unit Tests)**:
   > *"Write JUnit tests for CartViewModel to verify item price additions, quantity increments, delivery fee ($2.50), tax (8%), and grand total calculations, plus order ticket generation."*

---

### What AI Got Right & What It Got Wrong

#### ✅ What AI Got Right:
- **Coroutines & StateFlow Architecture**: The AI effortlessly set up clean Unidirectional Data Flow with `StateFlow` and Compose `collectAsStateWithLifecycle`, avoiding unnecessary re-renders.
- **Compose UI Layouts & Material 3 Styling**: Built modern, accessible UI components with warm coffee color palettes (`CoffeePrimary`, `CaramelSecondary`, `CoffeeBackground`), chips, and cards matching the design specification.

#### ❌ What AI Got Wrong & How It Was Fixed:
- **Wrong Retrofit Dependency Version**: The AI initially injected `com.squareup.retrofit2:retrofit:3.0.0` in `build.gradle.kts`. Retrofit `3.0.0` does not exist yet (the current stable release is `2.11.0`), which broke Gradle compilation.
- **Fix**: Replaced the invalid `3.0.0` dependency with `2.11.0` (`retrofit` & `converter-gson`) and added `material-icons-extended` for icon vectors.

---

## 🧪 Running Unit Tests

Run the unit tests using Gradle:
```bash
./gradlew testDebugUnitTest
```

---

## 📦 Building the Debug APK

To build the debug APK:
```bash
./gradlew app:assembleDebug
```
The generated APK file will be located at:
`app/build/outputs/apk/debug/app-debug.apk`
