Function un.RIndexOf
Exch $R0
Exch
Exch $R1
Push $R2
Push $R3
 
 StrCpy $R3 $R0
 StrCpy $R0 0
 IntOp $R0 $R0 + 1
  StrCpy $R2 $R3 1 -$R0
  StrCmp $R2 "" +2
  StrCmp $R2 $R1 +2 -3
 
 StrCpy $R0 -1
 
Pop $R3
Pop $R2
Pop $R1
Exch $R0
FunctionEnd
 
!macro un.RIndexOf Var Str Char
Push "${Char}"
Push "${Str}"
 Call un.RIndexOf
Pop "${Var}"
!macroend
!define un.RIndexOf "!insertmacro un.RIndexOf"
