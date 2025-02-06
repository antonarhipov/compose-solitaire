# Compose Solitaire

## Project Overview
Compose Solitaire is a modern implementation of the classic Solitaire card game using Jetpack Compose for Desktop. This project demonstrates how to build a desktop application using Kotlin and Compose Desktop framework.

## Technology Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose for Desktop
- **Build System**: Gradle (Kotlin DSL)

## Project Structure
```
compose-solitaire/
├── .junie/          # Project guidelines and documentation
├── .idea/           # IntelliJ IDEA configuration files
├── gradle/          # Gradle wrapper files
├── src/
│   └── main/
│       └── kotlin/  # Kotlin source files
├── build.gradle.kts # Gradle build configuration
├── settings.gradle.kts # Gradle settings
└── gradle.properties # Gradle properties
```

## Setup Instructions
1. **Prerequisites**:
   - JDK 11 or later
   - IntelliJ IDEA (recommended) or another IDE with Kotlin support

2. **Building the Project**:
   - Clone the repository
   - Open the project in IntelliJ IDEA
   - Let Gradle sync and download dependencies
   - Run the project using the 'desktop' run configuration

3. **Development**:
   - Main application entry point is in `src/main/kotlin/Main.kt`
   - The project uses Compose Desktop for UI components
   - Gradle is configured with Kotlin DSL for better IDE support

# Game requirements

Klondike Solitaire: Detailed Step-by-Step Guide

Introduction

Klondike Solitaire is one of the most popular single-player card games. The objective is to move all cards to the foundation piles, organized by suit in ascending order (from Ace to King). This guide provides a detailed step-by-step explanation of the rules and gameplay.

1. Game Setup

1.1 Deck

A standard 52-card deck is used (Jokers are removed).

1.2 Initial Card Layout

The game is played with four areas:

Tableau: The main playing area consisting of 7 columns of cards.

Stock Pile: The remaining deck, face down.

Waste Pile: A pile where drawn stock cards are placed face up.

Foundation Piles: Four empty piles where cards are stacked by suit in ascending order (Ace to King).

1.3 Tableau Setup

Deal the cards into 7 tableau columns as follows:

Column 1: 1 card (face up)

Column 2: 2 cards (top card face up, bottom card face down)

Column 3: 3 cards (top card face up, the rest face down)

Column 4: 4 cards (top card face up, the rest face down)

Column 5: 5 cards (top card face up, the rest face down)

Column 6: 6 cards (top card face up, the rest face down)

Column 7: 7 cards (top card face up, the rest face down)

The remaining 24 cards go into the stock pile (face down).

2. Game Objective

The goal is to move all 52 cards into the four foundation piles, organized by suit, in ascending order (A → 2 → 3 → ... → K).

3. Gameplay Rules

3.1 Moving Cards to the Foundation

If an Ace is revealed, move it to an empty foundation pile.

Cards are added to the foundation in ascending order (A → 2 → 3 → ... → K), maintaining the same suit.

3.2 Moving Cards within the Tableau

Cards can be stacked in descending order (K → Q → J → 10 → ... → 2 → A) and must alternate colors.

Example:

A red 7 can be placed on a black 8.

A black 5 can be placed on a red 6.

A group of sequentially stacked cards can be moved together.

3.3 Revealing Face-Down Cards

When moving a face-up card, if a face-down card is uncovered, flip it face up.

3.4 Filling Empty Columns

Only a King (or a sequence starting with a King) can be placed in an empty tableau column.

3.5 Using the Stock Pile

Draw cards from the stock pile and place them in the waste pile.

Only the top card of the waste pile is available for play.

You may place a waste pile card onto the tableau or foundation if it follows the placement rules.

If a drawn card is not playable, it remains in the waste pile.

3.6 Cycling Through the Stock

Once the stock pile is empty, you may turn over the waste pile to reuse the cards.

Depending on the game variation, there may be unlimited or limited resets of the stock.

4. Winning the Game

The game is won when all four foundation piles are completed, with all suits stacked in order from Ace to King.

5. Additional Variations

Draw 1 vs. Draw 3: Some versions allow drawing one card at a time from the stock, while others require drawing three cards at a time.

Timed Solitaire: Some versions add a time limit for an additional challenge.

Scoring System:

Moving a card to a foundation pile earns points.

Flipping a face-down card in the tableau earns points.

Moving cards within the tableau may also give points.

6. Strategy Tips

Always uncover face-down cards when possible.

Avoid moving cards to foundation piles too soon—keeping them in the tableau might allow better sequencing.

Try to create empty tableau columns early so Kings can be placed for better maneuverability.

Use stock pile draws wisely—if playing with three-card draw, remember the order of cards to optimize moves.

### Additional Notes
Once double-clicked, the game should find the first available place for this card and move this card to that position. The foundation slots have the priority.
The UI should display the moves counter and a timer 