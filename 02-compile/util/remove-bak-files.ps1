
Get-ChildItem -Path "$PSScriptRoot/../../01-code/is-packages" -Recurse -Filter "*.bak" | Remove-Item -Force
