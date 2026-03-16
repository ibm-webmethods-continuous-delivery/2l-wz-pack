
Get-ChildItem -Path "$PSScriptRoot/../../01-code/is-packages" -Recurse -Filter "*.frag" | Remove-Item -Force
Get-ChildItem -Path "$PSScriptRoot/../../01-code/is-packages" -Recurse -Filter "*.class" | Remove-Item -Force
Get-ChildItem -Path "$PSScriptRoot/../../01-code/is-packages" -Recurse -Filter "*.jar" | Remove-Item -Force
