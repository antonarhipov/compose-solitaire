# Compose Solitaire

A modern implementation of the classic Klondike Solitaire card game using Jetpack Compose for Desktop.

## Table of Contents
- [Game Rules](#game-rules)
- [UI Implementation](#ui-implementation)
- [Technical Architecture](#technical-architecture)
- [Testing Strategy](#testing-strategy)
- [Development Guidelines](#development-guidelines)

## Game Rules

### Setup
- Standard 52-card deck (no Jokers)
- 7 tableau columns with cards dealt as follows:
  - Column 1: 1 card (face up)
  - Column 2: 2 cards (1 face up)
  - Column 3: 3 cards (1 face up)
  - Column 4: 4 cards (1 face up)
  - Column 5: 5 cards (1 face up)
  - Column 6: 6 cards (1 face up)
  - Column 7: 7 cards (1 face up)
- Remaining cards form the stock pile

### Game Areas
1. **Tableau** (7 columns)
   - Cards must be stacked in descending order
   - Alternating colors (red/black)
   - Empty columns can only be filled with Kings
   - Multiple cards can be moved if in sequence

2. **Foundation** (4 piles)
   - Built up by suit from Ace to King
   - Cards can only be placed in ascending order
   - Each foundation represents one suit
   - Completion of all foundations is the win condition

3. **Stock Pile**
   - Remaining cards after initial deal
   - Cards can be drawn to waste pile
   - Can be recycled when empty

4. **Waste Pile**
   - Cards drawn from stock
   - Only top card is playable
   - Cards can be moved to tableau or foundation

### Game Mechanics
1. **Basic Moves**
   - Draw cards from stock to waste
   - Move cards between tableau columns
   - Build foundation piles by suit
   - Move single cards or sequences

2. **Special Rules**
   - Double-click auto-moves cards to foundation
   - Foundation piles have placement priority
   - Revealed face-down cards are automatically flipped
   - Empty columns can only receive Kings

## UI Implementation

### Layout Structure
1. **Top Section**
   - Stock and waste piles (left)
   - Foundation piles (right)
   - Score and timer display
   - Menu buttons

2. **Main Game Area**
   - 7 tableau columns
   - Visual card overlapping
   - Clear spacing between columns
   - Empty column indicators

3. **Status Bar**
   - Move counter
   - Timer
   - Game status
   - Menu options

### User Interactions
1. **Drag and Drop**
   - Smooth card movement
   - Visual feedback for valid moves
   - Drop zone highlighting
   - Invalid move indication

2. **Double-Click Actions**
   - Auto-move to foundation
   - Smart card placement
   - Visual feedback
   - Animation sequences

3. **Touch and Mouse Support**
   - Multi-platform input handling
   - Touch gesture support
   - Mouse hover effects
   - Click/tap feedback

### Visual Elements
1. **Card Design**
   - Clear suit and rank display
   - Face-up/down states
   - Selection highlighting
   - Drag preview

2. **Animations**
   - Card flip effects
   - Movement transitions
   - Deal animation
   - Win celebration

3. **Feedback Systems**
   - Valid move highlights
   - Error indicators
   - Progress feedback
   - Score updates

## Technical Architecture

### Core Components
1. **Data Models**
   - Card
   - Deck
   - Pile
   - GameState
   - Move

2. **State Management**
   - Immutable state classes
   - Event handling
   - Undo/redo system
   - Save/load functionality

3. **UI Components**
   - Card renderer
   - Pile layout
   - Drag-drop system
   - Animation controller

### Technology Stack
- Kotlin
- Jetpack Compose for Desktop
- Material 3 Design
- Coroutines for async operations

## Testing Strategy

### Unit Tests
1. **Game Logic**
   - Card operations
   - Move validation
   - Game state transitions
   - Win condition checking

2. **Model Tests**
   - Card properties
   - Deck operations
   - Pile management
   - State mutations

### UI Tests
1. **Component Testing**
   - Layout verification
   - Interaction handling
   - Animation behavior
   - State updates

2. **Integration Tests**
   - Complete game flows
   - User interactions
   - State persistence
   - Performance metrics

### Property-Based Tests
- Move validation rules
- Game state consistency
- Card sequence validation
- Edge case handling

## Development Guidelines

### Code Organization
- Clear package structure
- Separation of concerns
- Comprehensive documentation
- Consistent naming conventions

### Performance Considerations
- Efficient recomposition
- Resource optimization
- Animation performance
- Memory management

### Quality Assurance
- Comprehensive test coverage
- Regular performance profiling
- Accessibility compliance
- Cross-platform testing

## Getting Started
1. Clone the repository
2. Open in IntelliJ IDEA
3. Build with Gradle
4. Run the desktop application

## License
[MIT License](LICENSE)