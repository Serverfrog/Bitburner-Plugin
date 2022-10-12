# Bitburner-Plugin

![Build](https://github.com/Serverfrog/Bitburner-Plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/18338-bitburner-connector.svg)](https://plugins.jetbrains.com/plugin/18338-bitburner-connector)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/18338-bitburner-connector.svg)](https://plugins.jetbrains.com/plugin/18338-bitburner-connector)

<!-- Plugin description -->
This Plugin is for the Steam Game [Bitburner](https://store.steampowered.com/app/1812820/Bitburner/). It will create a
Right-Click option on Files with the Extension `.js`, `.ns`, `.txt` and `.script`.

To do this you need to get the API Auth Token from Bitburner.

- enable under `API Server` the Server and then Copy your Auth Token.
- Enter this Auth Token in IDEA in the Settings.

<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> >
  <kbd>Search for "Bitburner-Plugin"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/Serverfrog/Bitburner-Plugin/releases/latest) and install it manually
  using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Usage

### Bitburner game

- Enable the server
  - If the game starts in full screen, press F9 to display as a window.
  - Go to the top bar menu on _API Server_ > _Enable server_
- Copy the token
  - Go to the top bar menu on _API Server_ > _Copy Auth Token_

### WebStorm or IntelliJ IDE

- Paste the token in the IDE.
  - Open the settings (Ctrl+Alt+S), it will open a window.
  - On the left side, select _Tools_ > _BitBurner-Sync_.
  - On the right side is an option _Authentication Token_, paste the token inside the text box.
  - Click on the _Ok_ button.
- Start using.
  - In your project using your IDE, add one script (.js or .script), then it will show in your IDE a notification
    about the success action or an error.
  - You can create, modify, delete, rename or move scripts or folders with scripts. They will update automatically in
    the Bitburner game.

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
