; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{68611E54-42B6-437B-9857-411678FE2E68}
AppName=FinFamily
AppVerName=FinFamily 10.-3.400
AppPublisher=KK-Software
AppPublisherURL=http://www.sukuohjelmisto.fi
AppSupportURL=http://www.sukuohjelmisto.fi
AppUpdatesURL=http://www.sukuohjelmisto.fi
DefaultDirName={pf}\FinFamily
DefaultGroupName=FinFamily
OutputDir=C:\Users\Kalle\apualue\finfamily\swing
OutputBaseFilename=FinFamily402test
SetupIconFile=C:\Users\Kalle\apualue\finfamily\resources\images\Genealogia.ico
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "finnish"; MessagesFile: "compiler:Languages\Finnish.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "C:\Users\Kalle\apualue\finfamily\devc\Suku.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Kalle\apualue\finfamily\dist\suku.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Kalle\apualue\finfamily\dist\suku.sh"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Kalle\apualue\finfamily\dist\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\Kalle\apualue\finfamily\dist\properties\*"; DestDir: "{app}\properties"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\Kalle\apualue\finfamily\dist\resources\*"; DestDir: "{app}\resources"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\FinFamily"; Filename: "{app}\Suku.exe"
Name: "{group}\{cm:UninstallProgram,FinFamily}"; Filename: "{uninstallexe}"
Name: "{commondesktop}\FinFamily"; Filename: "{app}\Suku.exe"; Tasks: desktopicon
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\FinFamily"; Filename: "{app}\Suku.exe"; Tasks: quicklaunchicon

[Run]
Filename: "{app}\Suku.exe"; Description: "{cm:LaunchProgram,FinFamily}"; Flags: nowait postinstall skipifsilent




