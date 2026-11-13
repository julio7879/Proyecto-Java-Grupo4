<#
.SYNOPSIS
    Verifica la trazabilidad entre el documento maestro y la bóveda de Obsidian.

.DESCRIPTION
    Comprueba que:
      1. El maestro contiene los 35 RF, 22 RN, 16 HU y 25 RNF esperados.
      2. Cada RF aparece referenciado en la documentación de módulos.
      3. Cada RN aparece en el catálogo de reglas de negocio.
      4. Cada RNF aparece en su documento.
      5. Cada HU tiene archivo propio en 04-Historias con criterios Dado/Cuando/Entonces.
      6. Todos los wikilinks de la bóveda apuntan a archivos existentes.

    Sale con código 1 si alguna comprobación falla, para poder usarlo en un
    hook de pre-commit o en CI.

.EXAMPLE
    powershell -NoProfile -File ./scripts/check-traceability.ps1
#>
[CmdletBinding()]
param(
    [string]$Master,
    [string]$Vault
)

$ErrorActionPreference = 'Stop'
if (-not $Master) { $Master = Join-Path $PSScriptRoot '..\docs\vault\00-Documento-Maestro-v1.3.md' }
if (-not $Vault)  { $Vault  = Join-Path $PSScriptRoot '..\docs\vault' }

$Master = (Resolve-Path $Master).Path
$Vault  = (Resolve-Path $Vault).Path
$utf8 = [System.Text.UTF8Encoding]::new($false)

$fail = 0
$results = New-Object System.Collections.ArrayList

function Add-Result {
    param([string]$Check, [bool]$Ok, [string]$Detail)
    [void]$results.Add([pscustomobject]@{ Check = $Check; Ok = $Ok; Detail = $Detail })
    if (-not $Ok) { $script:fail++ }
}

function Get-Ids {
    param([string]$Text, [string]$Pattern)
    $ids = [regex]::Matches($Text, $Pattern) | ForEach-Object { $_.Value } | Sort-Object -Unique
    return @($ids)
}

$masterText = [System.IO.File]::ReadAllText($Master, $utf8)

$vaultFiles = Get-ChildItem -Path $Vault -Recurse -Filter *.md -File |
    Where-Object { $_.FullName -ne $Master }
$vaultText = ($vaultFiles | ForEach-Object { [System.IO.File]::ReadAllText($_.FullName, $utf8) }) -join "`n"

# --- 1. Inventario esperado del maestro ------------------------------------------
$masterRF  = Get-Ids $masterText 'RF-\d+'
$masterRN  = Get-Ids $masterText 'RN-\d+'
$masterHU  = Get-Ids $masterText 'HU-\d+'
$masterRNF = Get-Ids $masterText 'RNF-\d+'

Add-Result 'Maestro: RF'  ($masterRF.Count -eq 35)  ("{0}/35" -f $masterRF.Count)
Add-Result 'Maestro: RN'  ($masterRN.Count -eq 22)  ("{0}/22" -f $masterRN.Count)
Add-Result 'Maestro: HU'  ($masterHU.Count -eq 16)  ("{0}/16" -f $masterHU.Count)
Add-Result 'Maestro: RNF' ($masterRNF.Count -eq 25) ("{0}/25" -f $masterRNF.Count)

# --- 2. RF en modulos -------------------------------------------------------------
$moduleText = (Get-ChildItem -Path (Join-Path $Vault '01-Modulos'), (Join-Path $Vault '02-Arquitectura') -Filter *.md -Recurse -File |
    ForEach-Object { [System.IO.File]::ReadAllText($_.FullName, $utf8) }) -join "`n"
$missingRF = @($masterRF | Where-Object { $moduleText -notmatch [regex]::Escape($_) })

# --- 3. RN en el catalogo ---------------------------------------------------------
$rnPath = Join-Path $Vault '02-Arquitectura\Reglas-Negocio.md'
$rnText = if (Test-Path $rnPath) { [System.IO.File]::ReadAllText($rnPath, $utf8) } else { '' }
$missingRN = @($masterRN | Where-Object { $rnText -notmatch [regex]::Escape($_) })

# --- 4. RNF en su documento -------------------------------------------------------
$rnfPath = Join-Path $Vault '02-Arquitectura\Requerimientos-No-Funcionales.md'
$rnfText = if (Test-Path $rnfPath) { [System.IO.File]::ReadAllText($rnfPath, $utf8) } else { '' }
$missingRNF = @($masterRNF | Where-Object { $rnfText -notmatch [regex]::Escape($_) })

# --- 5. HU con archivo y criterios ------------------------------------------------
$huDir = Join-Path $Vault '04-Historias'
$missingHU = @()
$huSinCriterios = @()
foreach ($hu in $masterHU) {
    $file = Join-Path $huDir ($hu + '.md')
    if (-not (Test-Path $file)) { $missingHU += $hu; continue }
    $t = [System.IO.File]::ReadAllText($file, $utf8)
    if ($t -notmatch '(?i)dado' -or $t -notmatch '(?i)entonces') { $huSinCriterios += $hu }
}

# --- 6. Wikilinks resolubles ------------------------------------------------------
$names = @{}
foreach ($f in (Get-ChildItem -Path $Vault -Recurse -Filter *.md -File)) { $names[$f.BaseName] = $true }
$broken = New-Object System.Collections.ArrayList
foreach ($f in (Get-ChildItem -Path $Vault -Recurse -Filter *.md -File)) {
    $t = [System.IO.File]::ReadAllText($f.FullName, $utf8)
    foreach ($m in [regex]::Matches($t, '\[\[([^\]\|#]+)')) {
        $link = $m.Groups[1].Value.Trim()
        $base = ($link -split '/')[-1]
        if (-not $names.ContainsKey($base)) { [void]$broken.Add(("{0} -> {1}" -f $f.Name, $link)) }
    }
}

# --- 7. Enlaces Markdown a archivos locales ---------------------------------------
$localLinks = New-Object System.Collections.ArrayList
foreach ($f in (Get-ChildItem -Path $Vault -Recurse -Filter *.md -File)) {
    $raw = [System.IO.File]::ReadAllText($f.FullName, $utf8)
    # Fuera bloques de código: evita falsos positivos dentro de Mermaid o ejemplos.
    $clean = [regex]::Replace($raw, '(?ms)^```.*?^```', '')
    foreach ($m in [regex]::Matches($clean, '\[[^\]]*\]\(([^)\s]+)\)')) {
        $target = $m.Groups[1].Value
        if ($target -match '^(https?:|mailto:|#)') { continue }
        $target = ($target -split '#')[0]
        if (-not $target) { continue }
        $resolved = Join-Path $f.DirectoryName $target
        if (-not (Test-Path $resolved)) {
            [void]$localLinks.Add(("{0} -> {1}" -f $f.Name, $target))
        }
    }
}

# --- 8. Diagrama de clases y version declarada ------------------------------------
$clasesPath = Join-Path $Vault '02-Arquitectura\Diagrama-Clases.md'
$clasesOk = $false
if (Test-Path $clasesPath) {
    $clasesOk = ([System.IO.File]::ReadAllText($clasesPath, $utf8) -match 'classDiagram')
}
$versionMaster = if ($masterText -match '\*\*Versi[oó]n:\*\*\s*([0-9]+\.[0-9]+)') { $Matches[1] } else { '?' }
$mocPath = Join-Path $Vault '00-MOC\00-Indice-General.md'
$mocText = if (Test-Path $mocPath) { [System.IO.File]::ReadAllText($mocPath, $utf8) } else { '' }
$versionOk = $mocText -match ("Versi[oó]n del documento maestro:\*\*\s*" + [regex]::Escape($versionMaster))

# --- Resultados -------------------------------------------------------------------
Add-Result 'RF referenciados en modulos' ($missingRF.Count -eq 0)    $(if ($missingRF) { 'faltan: ' + ($missingRF -join ', ') } else { '35/35' })
Add-Result 'RN en catalogo de reglas'    ($missingRN.Count -eq 0)    $(if ($missingRN) { 'faltan: ' + ($missingRN -join ', ') } else { '22/22' })
Add-Result 'RNF documentados'            ($missingRNF.Count -eq 0)   $(if ($missingRNF) { 'faltan: ' + ($missingRNF -join ', ') } else { '25/25' })
Add-Result 'HU con archivo propio'       ($missingHU.Count -eq 0)    $(if ($missingHU) { 'faltan: ' + ($missingHU -join ', ') } else { '16/16' })
Add-Result 'HU con criterios Dado/Entonces' ($huSinCriterios.Count -eq 0) $(if ($huSinCriterios) { 'sin criterios: ' + ($huSinCriterios -join ', ') } else { '16/16' })
Add-Result 'Diagrama de clases UML'      $clasesOk                  $(if ($clasesOk) { 'classDiagram presente' } else { 'falta 02-Arquitectura/Diagrama-Clases.md o su bloque classDiagram' })
Add-Result 'Version del maestro en el MOC' $versionOk                ("maestro v{0} · MOC {1}" -f $versionMaster, $(if ($versionOk) { 'coincide' } else { 'NO coincide' }))
Add-Result 'Wikilinks resolubles'        ($broken.Count -eq 0)       $(if ($broken) { ($broken | Select-Object -Unique) -join '; ' } else { 'sin enlaces roscos' })
Add-Result 'Enlaces a archivos locales'  ($localLinks.Count -eq 0)  $(if ($localLinks) { ($localLinks | Select-Object -Unique) -join '; ' } else { 'todos resuelven' })

$results | ForEach-Object {
    $mark = if ($_.Ok) { '[OK]  ' } else { '[FAIL]' }
    '{0} {1,-38} {2}' -f $mark, $_.Check, $_.Detail
}

Write-Host ''
if ($fail -gt 0) {
    Write-Host ("Trazabilidad INCOMPLETA: {0} comprobacion(es) fallida(s)." -f $fail)
    exit 1
}
Write-Host 'Trazabilidad OK: maestro y boveda estan sincronizados.'
exit 0
