# --------------------------------------------------
# Defines
Name "Chord Scale Generator"
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION 1.4.0
!define COMPANY pluck-n-play
!define URL http://www.pluck-n-play.com
!define JRE_VERSION "1.6.0"
!define BUILD_DIR C:\Users\Andi\Eclipse\Build
!define BIT_VERSION win32.win32.x86_64
!define JAVA_FILE jre-windows-x64.exe
!define OUTPUT_FILE CSG-windows_64.exe

# --------------------------------------------------
# MUI defines
!define MUI_ICON "ico\logo.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall-full.ico"
!define MUI_UNFINISHPAGE_NOAUTOCLOSE
!define MUI_LANGDLL_REGISTRY_ROOT HKLM
!define MUI_LANGDLL_REGISTRY_KEY ${REGKEY}
#!define MUI_LANGDLL_REGISTRY_VALUENAME InstallerLanguage

# --------------------------------------------------
# Included files
!include Sections.nsh
!include MUI.nsh
#!include WinVer.nsh
!include "scripts\enumusersreg.nsh"
!include "scripts\stringindex.nsh"

# --------------------------------------------------
# Reserved Files
!insertmacro MUI_RESERVEFILE_LANGDLL
ReserveFile "jre.ini"
!insertmacro MUI_RESERVEFILE_INSTALLOPTIONS

# --------------------------------------------------
# Variables
Var StartMenuGroup
Var InstallJRE
Var JREPath

# --------------------------------------------------
# Installer pages

; Welcome Page
!define MUI_WELCOMEPAGE_TITLE $(welcome_title)
!define MUI_WELCOMEPAGE_TITLE_3LINES
!define MUI_WELCOMEPAGE_TEXT $(welcome_text)
!insertmacro MUI_PAGE_WELCOME

; Check JRE Page
Page custom CheckInstalledJRE

; JRE License Page
!define MUI_PAGE_HEADER_SUBTEXT $(java_license_header_subtext)
!define MUI_LICENSEPAGE_TEXT_BOTTOM $(java_license_text_bottom)
!define MUI_PAGE_CUSTOMFUNCTION_PRE myPreJavaLicense
!insertmacro MUI_PAGE_LICENSE $(JavaLicense)

; JRE Install Page
!define MUI_PAGE_HEADER_TEXT $(java_install_header_text)
!define MUI_PAGE_HEADER_SUBTEXT $(java_install_header_subtext)
!define MUI_INSTFILESPAGE_FINISHHEADER_TEXT $(java_install_finish_header_text)
!define MUI_INSTFILESPAGE_FINISHHEADER_SUBTEXT $(java_install_finish_header_subtext)
!insertmacro MUI_PAGE_INSTFILES

; License Page
!define MUI_PAGE_CUSTOMFUNCTION_PRE RestoreSections
!define MUI_PAGE_HEADER_SUBTEXT $(license_header_subtext)
!define MUI_LICENSEPAGE_TEXT_BOTTOM $(license_text_bottom)
!insertmacro MUI_PAGE_LICENSE $(MyLicense)

; Directory Page
!define MUI_PAGE_HEADER_SUBTEXT $(directory_header_subtext)
!define MUI_DIRECTORYPAGE_TEXT_TOP $(directory_text_top)
#!define MUI_PAGE_CUSTOMFUNCTION_LEAVE MyFuncDir
!insertmacro MUI_PAGE_DIRECTORY

; Startmenu Page
!define MUI_STARTMENUPAGE_REGISTRY_ROOT HKLM
!define MUI_STARTMENUPAGE_NODISABLE
!define MUI_STARTMENUPAGE_REGISTRY_KEY ${REGKEY}
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME StartMenuGroup
!define MUI_STARTMENUPAGE_DEFAULTFOLDER $(^Name)
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuGroup

; Install Page
!define MUI_PAGE_HEADER_TEXT $(install_header_text)
!define MUI_PAGE_HEADER_SUBTEXT $(install_header_subtext)
!insertmacro MUI_PAGE_INSTFILES

; Finish Page
!define MUI_FINISHPAGE_TITLE $(finish_title)
!define MUI_FINISHPAGE_TEXT $(finish_text)
!define MUI_FINISHPAGE_TEXT_REBOOT $(finish_reboot_text)
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_FINISHPAGE_RUN
!define MUI_FINISHPAGE_RUN_TEXT
!define MUI_FINISHPAGE_RUN_NOTCHECKED
!define MUI_PAGE_CUSTOMFUNCTION_PRE CreateControls
!define MUI_PAGE_CUSTOMFUNCTION_SHOW SetControlColours
!define MUI_PAGE_CUSTOMFUNCTION_LEAVE DisplayQuickLaunchAndDesktopIcon
!define MUI_FINISHPAGE_LINK $(finish_url)
!define MUI_FINISHPAGE_LINK_LOCATION $(finish_url)
!insertmacro MUI_PAGE_FINISH

; Confirm Uninstall Page
!define MUI_PAGE_HEADER_TEXT $(unconfirm_header_text)
!define MUI_PAGE_HEADER_SUBTEXT $(unconfirm_header_subtext)
!define MUI_UNCONFIRMPAGE_TEXT_TOP $(unconfirm_text_top)
!insertmacro MUI_UNPAGE_CONFIRM

; Uninstall Page
!define MUI_PAGE_HEADER_SUBTEXT $(uninstall_header_subtext)
!insertmacro MUI_UNPAGE_INSTFILES

# --------------------------------------------------
# Installer languages
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "German"

# --------------------------------------------------
# Installer attributes
OutFile ${BUILD_DIR}\${OUTPUT_FILE}
InstallDir "$PROGRAMFILES64\$(^Name)"
CRCCheck on
XPStyle on
ShowInstDetails show
VIProductVersion 1.0.0.0
VIAddVersionKey /LANG=${LANG_ENGLISH} ProductName $(^Name)
VIAddVersionKey /LANG=${LANG_ENGLISH} ProductVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} CompanyName "${COMPANY}"
VIAddVersionKey /LANG=${LANG_ENGLISH} CompanyWebsite "${URL}"
VIAddVersionKey /LANG=${LANG_ENGLISH} FileVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} FileDescription ""
VIAddVersionKey /LANG=${LANG_ENGLISH} LegalCopyright ""
InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails show
LicenseLangString MyLicense ${LANG_ENGLISH} "../licenses/csg_license_en.txt"
LicenseLangString MyLicense ${LANG_GERMAN} "../licenses/csg_license_de.txt"
LicenseLangString JavaLicense ${LANG_ENGLISH} "../licenses/jre6_license_en.rtf"
LicenseLangString JavaLicense ${LANG_GERMAN} "../licenses/jre6_license_de.rtf"

# --------------------------------------------------
# Installer sections

Section -installjre jre
    Push $0
    Push $1
    ;MessageBox MB_OK "Inside JRE Section"
    Strcmp $InstallJRE "yes" InstallJRE JREPathStorage

InstallJRE:
    DetailPrint "Starting the JRE installation" ;Adds message to the details view of the installer message
    File /oname=$TEMP\${JAVA_FILE} ${JAVA_FILE}
    ;MessageBox MB_OK "Installing JRE"
    DetailPrint "Launching JRE setup"
    ExecWait '"$TEMP\${JAVA_FILE}" /s /v\"/qn REBOOT=Suppress JAVAUPDATE=0 WEBSTARTICON=0\"' $0
    DetailPrint "Setup finished"
    Delete "$TEMP\${JAVA_FILE}"
    StrCmp $0 "0" InstallVerif 0
    Push "The JRE setup has been abnormally interrupted."
    Goto ExitInstallJRE

InstallVerif:
    DetailPrint "Checking the JRE Setup's outcome"
    ;MessageBox MB_OK "Checking JRE outcome"
    Push "${JRE_VERSION}"
    Call DetectJRE
    Pop $0      ; DetectJRE's return value
    StrCmp $0 "0" ExitInstallJRE 0
    StrCmp $0 "-1" ExitInstallJRE 0
    Goto JavaExeVerif
    Push "The JRE setup failed"
    Goto ExitInstallJRE

JavaExeVerif:
    IfFileExists $0 JREPathStorage 0
    Push "The following file : $0, cannot be found."
    Goto ExitInstallJRE

JREPathStorage:
    ;MessageBox MB_OK "Path Storage"
    !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "UserDefinedSection" "JREPath" $1
    StrCpy $JREPath $0
    Goto End

ExitInstallJRE:
    Pop $1
    ;MessageBox MB_OK "The setup is about to be interrupted for the following reason : $1"
    Pop $1    ; Restore $1
    Pop $0    ; Restore $0
    Abort

End:
    Pop $1    ; Restore $1
    Pop $0    ; Restore $0

SectionEnd

Section -Main SecAppFiles
    SetShellVarContext all
    SetOutPath $INSTDIR\configuration
    SetOverwrite on
    File /r "${BUILD_DIR}\${BIT_VERSION}\Chord Scale Generator\configuration\*"
    SetOutPath $INSTDIR\features
    File /r "${BUILD_DIR}\${BIT_VERSION}\Chord Scale Generator\features\*"
    SetOutPath $INSTDIR\plugins
    File /r "${BUILD_DIR}\${BIT_VERSION}\Chord Scale Generator\plugins\*"
    SetOutPath $INSTDIR
    File "${BUILD_DIR}\${BIT_VERSION}\Chord Scale Generator\.eclipseproduct"
    File "${BUILD_DIR}\${BIT_VERSION}\Chord Scale Generator\ChordScaleGenerator.exe"
    CreateDirectory "$SMPROGRAMS\$StartMenuGroup"
    SetOutPath $INSTDIR
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\$(^Name).lnk" $INSTDIR\ChordScaleGenerator.exe
    SetOutPath $SMPROGRAMS\$StartMenuGroup
    CreateShortcut $SMPROGRAMS\$StartMenuGroup\Homepage.lnk http://www.pluck-n-play.com
    WriteRegStr HKLM "${REGKEY}\Components" Main 1
SectionEnd

Section -post SecCreateShortcut
    SetShellVarContext all
    WriteRegStr HKLM "${REGKEY}" Path $INSTDIR
    SetOutPath $INSTDIR
    WriteUninstaller $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    SetOutPath $SMPROGRAMS\$StartMenuGroup
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\$(^UninstallLink).lnk" $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_END
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" Publisher "${COMPANY}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" URLInfoAbout "${URL}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1
SectionEnd

# --------------------------------------------------
# Macro for selecting uninstaller sections
!macro SELECT_UNSECTION SECTION_NAME UNSECTION_ID
    Push $R0
    ReadRegStr $R0 HKLM "${REGKEY}\Components" "${SECTION_NAME}"
    StrCmp $R0 1 0 next${UNSECTION_ID}
    !insertmacro SelectSection "${UNSECTION_ID}"
    GoTo done${UNSECTION_ID}
next${UNSECTION_ID}:
    !insertmacro UnselectSection "${UNSECTION_ID}"
done${UNSECTION_ID}:
    Pop $R0
!macroend

# --------------------------------------------------
# Uninstaller sections

Section /o -un.Main UNSEC0000
    SetShellVarContext all
    Delete /REBOOTOK $SMPROGRAMS\$StartMenuGroup\Homepage.lnk
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\$(^Name).lnk"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\$(^Name).lnk"
    Delete /REBOOTOK "$QUICKLAUNCH\$(^Name).lnk"
    Delete /REBOOTOK "$DESKTOP\$(^Name).lnk"
    Delete /REBOOTOK $INSTDIR\.eclipseproduct
    RmDir /r /REBOOTOK $INSTDIR\plugins
    RmDir /r /REBOOTOK $INSTDIR\features
    RmDir /r /REBOOTOK $INSTDIR\configuration
    DeleteRegValue HKLM "${REGKEY}\Components" Main
SectionEnd

Section -un.post UNSEC0001
    SetShellVarContext all
    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\$(^UninstallLink).lnk"
    Delete /REBOOTOK $INSTDIR\uninstall.exe
    DeleteRegValue HKLM "${REGKEY}" StartMenuGroup
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir /r /REBOOTOK $SMPROGRAMS\$StartMenuGroup
    RmDir /r /REBOOTOK $INSTDIR
    RmDir /r /REBOOTOK "$PROFILE\.chordScaleGenerator"
    ${un.EnumUsersReg} un.EraseAppDataCB temp.key
SectionEnd

Function "un.EraseAppDataCB"
  Pop $0
  ReadRegStr $0 HKU "$0\Software\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders" "AppData"
  ${un.RIndexOf} $1 $0 "\"
  StrLen $2 $0
  IntOp $3 $2 - $1
  StrCpy $4 $0 $3
  RMDir /r /REBOOTOK "$4\.chordScaleGenerator"
FunctionEnd

# --------------------------------------------------
# Installer functions

Function .onInit
    SetRegView 64
    InitPluginsDir
    !insertmacro MUI_LANGDLL_DISPLAY
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT "jre.ini"
    Call SetupSections
FunctionEnd

#Function MyFuncDir
#    ${If} ${IsWinVista}
#        StrLen $0 $PROGRAMFILES
#        StrCpy $1 $INSTDIR $0
#        StrCmp $1 $PROGRAMFILES box
#        StrLen $0 $PROGRAMFILES32
#        StrCpy $1 $INSTDIR $0
#        StrCmp $1 $PROGRAMFILES32 box
#        StrLen $0 $PROGRAMFILES64
#        StrCpy $1 $INSTDIR $0
#        StrCmp $1 $PROGRAMFILES64 box noabort
#        box:
#            MessageBox MB_YESNO|MB_ICONEXCLAMATION|MB_DEFBUTTON2 $(vista_directory_choice_prompt) IDYES noabort
#              Abort
#        noabort:
#    ${EndIf}
#FunctionEnd

Function myPreJavaLicense
  Strcmp $InstallJRE "no" cancel end
  cancel:
    SetAutoClose true
    Abort
  end:
FunctionEnd

Function SetupSections
  !insertmacro SelectSection ${jre}
  !insertmacro UnselectSection ${SecAppFiles}
  !insertmacro UnselectSection ${SecCreateShortcut}
FunctionEnd

Function RestoreSections
  SetAutoClose false
  !insertmacro UnselectSection ${jre}
  !insertmacro SelectSection ${SecAppFiles}
  !insertmacro SelectSection ${SecCreateShortcut}
FunctionEnd

# --------------------------------------------------
# Uninstaller functions
Function un.onInit
    SetRegView 64
    ReadRegStr $INSTDIR HKLM "${REGKEY}" Path
    !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuGroup
    !insertmacro MUI_UNGETLANGUAGE
    !insertmacro SELECT_UNSECTION Main ${UNSEC0000}
FunctionEnd

# --------------------------------------------------
# Finish Page functions

Function CreateControls
  ; Quicklaunch short cut.
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Settings" "NumFields" "6"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 6" "Type" "CheckBox"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 6" "Text" $(finish_quick_launch_text)
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 6" "Left" "120"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 6" "Right" "-10"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 6" "Top" "120"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 6" "Bottom" "132"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 6" "State" "1"
  ; Desktop short cut.
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Settings" "NumFields" "7"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 7" "Type" "CheckBox"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 7" "Text" $(finish_desktop_text)
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 7" "Left" "120"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 7" "Right" "-10"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 7" "Top" "134"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 7" "Bottom" "146"
  WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 7" "State" "1"

  ; Modify location of Launch checkbox
  IfRebootFlag Setreboot Noreboot
  Noreboot:
    WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 4" "Top" "0"
    WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 4" "Bottom" "0"
    Goto End

  Setreboot:
    WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 4" "Top" "92"
    WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 4" "Bottom" "104"
    WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 5" "Top" "106"
    WriteINIStr "$PLUGINSDIR\iospecial.ini" "Field 5" "Bottom" "118"

  End:
FunctionEnd

Function SetControlColours
  ReadINIStr $0 "$PLUGINSDIR\iospecial.ini" "Field 6" "HWND"
  SetCtlColors $0 0x000000 0xFFFFFF
  ReadINIStr $0 "$PLUGINSDIR\iospecial.ini" "Field 7" "HWND"
  SetCtlColors $0 0x000000 0xFFFFFF
FunctionEnd

Function DisplayQuickLaunchAndDesktopIcon
  SetOutPath $INSTDIR
  ReadINIStr $0 "$PLUGINSDIR\iospecial.ini" "Field 6" "State"
  StrCmp $0 "0" end
    CreateShortCut "$QUICKLAUNCH\$(^Name).lnk" "$INSTDIR\ChordScaleGenerator.exe"
  end:
  ReadINIStr $0 "$PLUGINSDIR\iospecial.ini" "Field 7" "State"
  StrCmp $0 "0" finish
    CreateShortCut "$DESKTOP\$(^Name).lnk" "$INSTDIR\ChordScaleGenerator.exe"
  finish:
FunctionEnd

# --------------------------------------------------
# JRE functions

Function CheckInstalledJRE
  ;MessageBox MB_OK "Checking Installed JRE Version"
  Push "${JRE_VERSION}"
  Call DetectJRE
  ;MessageBox MB_OK "Done checking JRE version"
  Exch $0   ; Get return value from stack
  StrCmp $0 "0" NoFound
  StrCmp $0 "-1" FoundOld
  Goto JREAlreadyInstalled

FoundOld:
  ;MessageBox MB_OK "Old JRE found"
  !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "Field 1" "Text" $(java_check_text_old_jre)
  !insertmacro MUI_HEADER_TEXT "$(java_check_header_text)" "$(java_check_header_subtext)"
  !insertmacro MUI_INSTALLOPTIONS_DISPLAY_RETURN "jre.ini"
  Goto MustInstallJRE

NoFound:
  ;MessageBox MB_OK "JRE not found"
  !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "Field 1" "Text" $(java_check_text_old_jre)
  !insertmacro MUI_HEADER_TEXT "$(java_check_header_text)" "$(java_check_header_subtext)"
  !insertmacro MUI_INSTALLOPTIONS_DISPLAY_RETURN "jre.ini"
  Goto MustInstallJRE

MustInstallJRE:
  Exch $0   ; $0 now has the installoptions page return value
  ; Do something with return value here
  Pop $0    ; Restore $0
  StrCpy $InstallJRE "yes"
  SetRebootFlag true
  Return

JREAlreadyInstalled:
  ;MessageBox MB_OK "JRE already installed"
  StrCpy $InstallJRE "no"
  SetRebootFlag false
  !insertmacro MUI_INSTALLOPTIONS_WRITE "jre.ini" "UserDefinedSection" "JREPath" $JREPATH
  Pop $0        ; Restore $0
  Return

FunctionEnd

; Returns: 0 - JRE not found. -1 - JRE found but too old. Otherwise - Path to JAVA EXE
; DetectJRE. Version requested is on the stack.
; Returns (on stack)    "0" on failure (java too old or not installed), otherwise path to java interpreter
; Stack value will be overwritten!

Function DetectJRE
  Exch $0   ; Get version requested
            ; Now the previous value of $0 is on the stack, and the asked for version of JDK is in $0
  Push $1   ; $1 = Java version string (ie 1.5.0)
  Push $2   ; $2 = Javahome
  Push $3   ; $3 and $4 are used for checking the major/minor version of java
  Push $4
  ;MessageBox MB_OK "Detecting JRE"
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ;MessageBox MB_OK "Read : $1"
  StrCmp $1 "" DetectTry2
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$1" "JavaHome"
  ;MessageBox MB_OK "Read 3: $2"
  StrCmp $2 "" DetectTry2
  Goto GetJRE

DetectTry2:
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
  ;MessageBox MB_OK "Detect Read : $1"
  StrCmp $1 "" NoFound
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$1" "JavaHome"
  ;MessageBox MB_OK "Detect Read 3: $2"
  StrCmp $2 "" NoFound

GetJRE:
  ; $0 = version requested. $1 = version found. $2 = javaHome
  ;MessageBox MB_OK "Getting JRE"
  IfFileExists "$2\bin\java.exe" 0 NoFound
  StrCpy $3 $0 1            ; Get major version. Example: $1 = 1.5.0, now $3 = 1
  StrCpy $4 $1 1            ; $3 = major version requested, $4 = major version found
  ;MessageBox MB_OK "Want $3 , found $4"
  IntCmp $4 $3 0 FoundOld FoundNew
  StrCpy $3 $0 1 2
  StrCpy $4 $1 1 2          ; Same as above. $3 is minor version requested, $4 is minor version installed
  ;MessageBox MB_OK "Want $3 , found $4"
  IntCmp $4 $3 FoundNew FoundOld FoundNew

NoFound:
  ;MessageBox MB_OK "JRE not found"
  Push "0"
  Goto DetectJREEnd

FoundOld:
  ;MessageBox MB_OK "JRE too old: $3 is older than $4"
  ;Push ${TEMP2}
  Push "-1"
  Goto DetectJREEnd
FoundNew:
  ;MessageBox MB_OK "JRE is new: $3 is newer than $4"

  Push "$2\bin\java.exe"
;  Push "OK"
;  Return
   Goto DetectJREEnd
DetectJREEnd:
    ; Top of stack is return value, then r4,r3,r2,r1
    Exch    ; => r4,rv,r3,r2,r1,r0
    Pop $4  ; => rv,r3,r2,r1r,r0
    Exch    ; => r3,rv,r2,r1,r0
    Pop $3  ; => rv,r2,r1,r0
    Exch    ; => r2,rv,r1,r0
    Pop $2  ; => rv,r1,r0
    Exch    ; => r1,rv,r0
    Pop $1  ; => rv,r0
    Exch    ; => r0,rv
    Pop $0  ; => rv
FunctionEnd


# --------------------------------------------------
# Installer Language Strings

LangString welcome_title ${LANG_ENGLISH} "Welcome to the $(^Name) Setup Wizard"
LangString welcome_title ${LANG_GERMAN} "Willkommen beim Installations-Assistenten für den $(^Name)"

LangString welcome_text ${LANG_ENGLISH} "This wizard will guide you through the installation of the $(^Name).\n\nIt is recommended that you close all other applications before starting Setup. This will make it possible to update relevant system files without having to reboot your computer.\n\nClick Next to continue."
LangString welcome_text ${LANG_GERMAN} "Dieser Assistent wird Sie durch die Installation des $(^Name)s begleiten.\n\nEs wird empfohlen, vor der Installation alle anderen Programme zu schließen, damit bestimmte Systemdateien ohne Neustart ersetzt werden können.\n\nKlicken Sie auf Weiter um fortzufahren."

LangString java_check_header_text ${LANG_ENGLISH} "Check for Java Runtime Environment"
LangString java_check_header_text ${LANG_GERMAN} "Überprüfe Java Laufzeitumgebung"

LangString java_check_header_subtext ${LANG_ENGLISH} "The $(^Name) requires an installed Java Runtime Environment on your computer."
LangString java_check_header_subtext ${LANG_GERMAN} "Der $(^Name) benötigt eine installierte Java Laufzeitumgebung auf ihrem Computer."

LangString java_check_text_no_jre ${LANG_ENGLISH} "No Java Runtime Environment could be found on your computer.\n\nClick Next to start the installation of the Java Runtime Environment (Version 6.0)."
LangString java_check_text_no_jre ${LANG_GERMAN} "Es konnte keine Java Laufzeitumgebung auf ihrem Computer gefunden werden.\n\nKlicken Sie Weiter, um die Java Laufzeitumgebung (Version 6.0) zu installieren."

LangString java_check_text_old_jre ${LANG_ENGLISH} "The $(^Name) requires a more recent version of the Java Runtime Environment than the one found on your computer.\n\nClick Next to start the installation of the Java Runtime Environment (Version 6.0)."
LangString java_check_text_old_jre ${LANG_GERMAN} "Der $(^Name) benötigt eine neuere Version der Java Laufzeitumgebung als die Version, die auf ihrem Computer gefunden wurde.\n\nKlicken Sie Weiter, um die Java Laufzeitumgebung (Version 6.0) zu installieren."

LangString java_license_header_subtext ${LANG_ENGLISH} "Please review the license terms before installing the Java Runtime Environment."
LangString java_license_header_subtext ${LANG_GERMAN} "Bitte lesen Sie die Lizenzbedingungen durch, bevor Sie mit der Installation der Java Laufzeitumgebung fortfahren."

LangString java_license_text_bottom ${LANG_ENGLISH} "If you accept the terms of the agreement, click I Agree to continue. You must accept the agreement to install the Java Runtime Environment."
LangString java_license_text_bottom ${LANG_GERMAN} "Wenn Sie alle Bedingungen des Abkommens akzeptieren, klicken Sie auf Annehmen. Sie müssen die Lizenzvereinbarung anerkennen, um die Java Laufzeitumgebung installieren zu können."

LangString java_install_header_text ${LANG_ENGLISH} "Installing Java Runtime Environment..."
LangString java_install_header_text ${LANG_GERMAN} "Installiere Java Laufzeitumgebung..."

LangString java_install_header_subtext ${LANG_ENGLISH} "Please wait while the Java Runtime Environment is being installed. Please be patient, this could take some time."
LangString java_install_header_subtext ${LANG_GERMAN} "Bitte warten Sie, während die Java Laufzeitumgebung installiert wird. Bitte haben Sie etwas Geduld, dies kann einige Minuten in Anspruch nehmen."

LangString java_install_finish_header_text ${LANG_ENGLISH} "Installation complete"
LangString java_install_finish_header_text ${LANG_GERMAN} "Die Installation ist vollständig."

LangString java_install_finish_header_subtext ${LANG_ENGLISH} "Setup of Java Runtime Environment was completed successfully."
LangString java_install_finish_header_subtext ${LANG_GERMAN} "Die Installation der Java Laufzeitumgebung wurde erfolgreich abgeschlossen."

LangString license_header_subtext ${LANG_ENGLISH} "Please review the license terms before installing the $(^Name)."
LangString license_header_subtext ${LANG_GERMAN} "Bitte lesen Sie die Lizenzbedingungen durch, bevor Sie mit der Installation fortfahren."

LangString license_text_bottom ${LANG_ENGLISH} "If you accept the terms of the agreement, click I Agree to continue. You must accept the agreement to install the $(^Name)."
LangString license_text_bottom ${LANG_GERMAN} "Wenn Sie alle Bedingungen des Abkommens akzeptieren, klicken Sie auf Annehmen. Sie müssen die Lizenzvereinbarung anerkennen, um den $(^Name) installieren zu können."

LangString directory_header_subtext ${LANG_ENGLISH} "Choose the folder in which to install the $(^Name)."
LangString directory_header_subtext ${LANG_GERMAN} "Wählen Sie das Verzeichnis, in das der $(^Name) installiert werden soll."

LangString directory_text_top ${LANG_ENGLISH} "Setup will install the $(^Name) in the following folder. To install in a different folder, click Browse and select another folder. Click Next to continue."
LangString directory_text_top ${LANG_GERMAN} "Der $(^Name) wird in das unten angegebene Verzeichnis installiert. Falls Sie in ein anderes Verzeichnis installieren möchten, klicken Sie auf Durchsuchen und wählen Sie ein anderes Verzeichnis aus. Klicken Sie auf Weiter, um fortzufahren."

#LangString vista_directory_choice_prompt ${LANG_ENGLISH} "Warning for Windows Vista:$\n$\nBy default, Microsoft Windows Vista does not allow user level accounts to write files in the '$PROGRAMFILES' directory. Since the Chord Scale Generator stores data in its install directory during program updates you should install the Chord Scale Generator outside the '$PROGRAMFILES' directory. Otherwise, it could be required to launch the Chord Scale Generator explicitly with administrator rights (feature 'Run as administrator').$\n$\nDo you still want to continue?"
#LangString vista_directory_choice_prompt ${LANG_GERMAN} "Hinweis für Windows Vista:$\n$\nMicrosoft Windows Vista erlaubt es nicht allen angelegten Benutzern, Dateien in den Ordner '$PROGRAMFILES' zu schreiben. Da der Chord Scale Generator bei Programm-Aktualisierungen Dateien direkt in den Installationsordner schreibt, solltest du den Chord Scale Generator außerhalb des '$PROGRAMFILES' Ordners installieren. Anderenfalls kann es ggf. notwendig sein, den Chord Scale Generator für Aktualisierungen explizit mit Administrator-Rechten zu starten (Funktion 'Als Administrator ausführen').$\n$\nMöchtest du trotzdem fortfahren?"

LangString install_header_text ${LANG_ENGLISH} "Installing..."
LangString install_header_text ${LANG_GERMAN} "Installiere..."

LangString install_header_subtext ${LANG_ENGLISH} "Please wait while the $(^Name) is being installed."
LangString install_header_subtext ${LANG_GERMAN} "Bitte warten Sie, während der $(^Name) installiert wird."

LangString finish_title ${LANG_ENGLISH} "Completing the $(^Name) Setup Wizard"
LangString finish_title ${LANG_GERMAN} "Die Installation des $(^Name)s ist abgeschlossen."

LangString finish_text ${LANG_ENGLISH} "The $(^Name) has been installed on your computer.\n\nClick Finish to close the wizard."
LangString finish_text ${LANG_GERMAN} "Der $(^Name) wurde auf ihrem Computer installiert.\n\nKlicken Sie auf Fertig stellen, um den Installations-Assistenten zu schließen."

LangString finish_reboot_text ${LANG_ENGLISH} "Your computer must be restarted in order to complete the installation of the $(^Name). Do you want to reboot now?"
LangString finish_reboot_text ${LANG_GERMAN} "Windows muss neu gestartet werden, um die Installation des $(^Name)s zu vervollständigen. Möchten Sie Windows jetzt neu starten?"

#LangString finish_run_text ${LANG_ENGLISH} "&Run $(^Name)"
#LangString finish_run_text ${LANG_GERMAN} "$(^Name) &ausführen"

LangString finish_quick_launch_text ${LANG_ENGLISH} "Add Shortcut to &Quick Launch"
LangString finish_quick_launch_text ${LANG_GERMAN} "&Schnellstart-Symbol hinzufügen"

LangString finish_desktop_text ${LANG_ENGLISH} "Add Shortcut to &Desktop"
LangString finish_desktop_text ${LANG_GERMAN} "&Desktop-Symbol hinzufügen"

LangString finish_url ${LANG_ENGLISH} "http://www.pluck-n-play.com"
LangString finish_url ${LANG_GERMAN} "http://www.pluck-n-play.de"

LangString unconfirm_header_text ${LANG_ENGLISH} "Uninstall the $(^Name)"
LangString unconfirm_header_text ${LANG_GERMAN} "Deinstallation des $(^Name)s"

LangString unconfirm_header_subtext ${LANG_ENGLISH} "Remove the $(^Name) from your computer."
LangString unconfirm_header_subtext ${LANG_GERMAN} "Der $(^Name) wird von ihrem Computer entfernt."

LangString unconfirm_text_top ${LANG_ENGLISH} "The $(^Name) will be uninstalled from the following folder. Click Uninstall to start the uninstallation."
LangString unconfirm_text_top ${LANG_GERMAN} "Der $(^Name) wird aus dem unten angegebenen Verzeichnis entfernt. Klicken Sie auf Deinstallieren, um die Deinstallation zu starten."

LangString uninstall_header_subtext ${LANG_ENGLISH} "Please wait while the $(^Name) is being uninstalled."
LangString uninstall_header_subtext ${LANG_GERMAN} "Bitte warten Sie, während der $(^Name) entfernt wird."

LangString ^UninstallLink ${LANG_ENGLISH} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_GERMAN} "Deinstalliere $(^Name)"
