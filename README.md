# Darwin World Simulator

**JavaFX simulation of an evolving ecosystem with animals, plants, genetics, and configurable world rules.**

## Overview

Darwin World Simulator is a JavaFX-based simulation of an evolving ecosystem inspired by evolutionary processes and genetic algorithms. The application models a 2D world populated by animals and plants, where organisms move, consume resources, reproduce, mutate, and compete for survival over time.

The simulation takes place on a rectangular map divided into steppe and preferred plant-growth regions. Plants grow randomly, with a higher probability in selected areas. Animals move according to cyclic genomes, lose energy every day, gain energy by consuming plants, and reproduce if they meet the required energy threshold.

This project was developed as part of an Object-Oriented Programming course and demonstrates practical use of object-oriented design, simulation modeling, GUI programming, and configurable application logic.

## Features

- JavaFX graphical interface
- Configurable simulation parameters
- Real-time map visualization
- Animal movement based on cyclic genomes
- Plant growth with preferred growth regions
- Energy-based survival system
- Reproduction with genome crossover and mutation
- Daily simulation cycle
- Live statistics panel
- Pause and resume functionality

## Simulation Rules

Each day in the simulation consists of:
1. Removing dead animals
2. Rotating and moving animals
3. Consuming plants
4. Reproducing animals on the same field
5. Growing new plants

Each animal is defined by:
- position,
- orientation,
- energy,
- genome,
- active gene index

Plants serve as an energy source and appear according to the growth rules defined by the simulation configuration.

## Technologies

- Java
- JavaFX
- Gradle
- JUnit

## Project Structure

- `src/main/java` — application source code
- `src/main/resources` — resources and UI files
- `model` — simulation logic, map, animals, plants
- `engine` — simulation loop and state updates
- `ui` — JavaFX views and controllers
- `config` — simulation configuration handling
- `stats` — statistics collection and presentation

## My Contribution

This repository is my portfolio copy of a team academic project.

My contribution included:
- implementing parts of the simulation logic
- working on selected JavaFX UI elements
- developing and improving domain classes
- debugging and validating simulation behavior
- contributing to project integration and testing

## How to Run

Clone the repository:

```bash
git clone https://github.com/mikimiki102/darwin-world-simulator.git
cd darwin-world-simulator
```

Run the application with Gradle:

```bash
./gradlew run
```

Or open the project in IntelliJ IDEA and run the main JavaFX application class.

## Why this project is valuable

This project demonstrates:
- object-oriented design in practice
- stateful simulation modeling
- GUI application development
- handling evolving entities and rule-based systems
- translating a larger specification into a working application

## Notes

This project is based on an academic assignment specification.  
The repository is intended for educational and portfolio presentation.

---

# Polski

**Symulacja ewoluującego ekosystemu w JavaFX z udziałem zwierząt, roślin, genetyki i konfigurowalnych zasad świata.**

## Opis projektu

Darwin World Simulator to aplikacja w JavaFX przedstawiająca symulację ewoluującego ekosystemu inspirowaną procesami ewolucyjnymi i algorytmami genetycznymi. Program modeluje dwuwymiarowy świat zamieszkany przez zwierzęta i rośliny, w którym organizmy poruszają się, zdobywają pożywienie, rozmnażają się, mutują i konkurują o przetrwanie.

Symulacja działa na prostokątnej mapie podzielonej na obszary stepowe i preferowane strefy wzrostu roślin. Rośliny pojawiają się losowo, ale z większym prawdopodobieństwem w wybranych obszarach. Zwierzęta poruszają się zgodnie ze swoim cyklicznym genomem, tracą energię każdego dnia, zyskują ją dzięki zjadaniu roślin i rozmnażają się po osiągnięciu odpowiedniego poziomu energii.

Projekt został zrealizowany w ramach kursu Programowania Obiektowego i pokazuje praktyczne zastosowanie projektowania obiektowego, modelowania symulacji, tworzenia GUI oraz konfigurowalnej logiki aplikacji.

## Funkcjonalności

- graficzny interfejs użytkownika w JavaFX
- konfigurowalne parametry symulacji
- wizualizacja mapy w czasie rzeczywistym
- ruch zwierząt sterowany cyklicznym genomem
- wzrost roślin z uwzględnieniem obszarów preferowanych
- system przetrwania oparty na energii
- rozmnażanie z krzyżowaniem genomów i mutacjami
- dzienny cykl symulacji
- panel statystyk na żywo
- możliwość zatrzymywania i wznawiania symulacji

## Zasady symulacji

Każdy dzień symulacji składa się z następujących etapów:
1. usunięcie martwych zwierząt
2. obrót i ruch zwierząt
3. konsumpcja roślin
4. rozmnażanie zwierząt znajdujących się na tym samym polu
5. wzrost nowych roślin

Każde zwierzę posiada:
- pozycję
- kierunek
- energię
- genom
- indeks aktualnie aktywnego genu

Rośliny stanowią źródło energii i pojawiają się zgodnie z regułami wzrostu określonymi przez konfigurację symulacji.

## Technologie

- Java
- JavaFX
- Gradle
- JUnit

## Struktura projektu

- `src/main/java` — kod źródłowy aplikacji
- `src/main/resources` — zasoby i pliki interfejsu
- `model` — logika symulacji, mapa, zwierzęta, rośliny
- `engine` — pętla symulacji i aktualizacja stanu
- `ui` — widoki i kontrolery JavaFX
- `config` — obsługa konfiguracji symulacji
- `stats` — zbieranie i prezentacja statystyk

## Mój wkład

To repozytorium stanowi moją wersję portfolio projektu zespołowego stworzonego na studiach.

Mój wkład obejmował:
- implementację części logiki symulacji
- pracę nad wybranymi elementami interfejsu JavaFX
- rozwijanie klas domenowych
- debugowanie i weryfikację poprawności działania symulacji
- udział w integracji projektu oraz testowaniu

## Jak uruchomić

Sklonuj repozytorium:

```bash
git clone https://github.com/mikimiki102/darwin-world-simulator.git
cd darwin-world-simulator
```

Uruchom aplikację przez Gradle:

```bash
./gradlew run
```

Albo otwórz projekt w IntelliJ IDEA i uruchom główną klasę aplikacji JavaFX.

## Dlaczego ten projekt jest wartościowy

Projekt pokazuje w praktyce:
- zastosowanie programowania obiektowego
- modelowanie symulacji o zmiennym stanie
- tworzenie aplikacji desktopowych z GUI
- implementację systemu opartego na regułach
- przełożenie rozbudowanej specyfikacji na działający program

## Uwagi

Projekt bazuje na specyfikacji zadania akademickiego.  
Repozytorium zostało przygotowane w celach edukacyjnych i portfolio.
