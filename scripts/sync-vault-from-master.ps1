<#
.SYNOPSIS
    Genera los documentos derivados de la bóveda a partir del documento maestro.

.DESCRIPTION
    El documento maestro (docs/vault/00-Documento-Maestro-v1.3.md) es la única
    fuente de verdad. Este script extrae de él las secciones que la bóveda
    publica y las reescribe, de modo que la documentación nunca se desincronice
    por edición manual de los derivados.

    Archivos generados:
      docs/vault/02-Arquitectura/Modelo-Relacional.md          (maestro §2.1-§2.4)
      docs/vault/02-Arquitectura/Requerimientos-No-Funcionales.md (maestro §5.1-§5.3)
      docs/vault/02-Arquitectura/Transacciones-JDBC.md         (maestro §5.6)
      docs/vault/03-Gestion/Plan-Iteraciones.md                (maestro §8)
      docs/vault/04-Historias/HU-01.md ... HU-16.md            (maestro §6)

.EXAMPLE
    pwsh ./scripts/sync-vault-from-master.ps1
#>
[CmdletBinding()]
param(
    [string]$Master,
    [string]$Vault
)

$ErrorActionPreference = 'Stop'
if (-not $Master) { $Master = Join-Path $PSScriptRoot '..\docs\vault\00-Documento-Maestro-v1.3.md' }
if (-not $Vault)  { $Vault  = Join-Path $PSScriptRoot '..\docs\vault' }
$utf8 = [System.Text.UTF8Encoding]::new($false)

$Master = (Resolve-Path $Master).Path
$Vault  = (Resolve-Path $Vault).Path

$lines = [System.IO.File]::ReadAllLines($Master, $utf8)

function Find-Line {
    param([string]$Pattern, [int]$From = 0, [string]$What = $Pattern)
    for ($i = $From; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match $Pattern) { return $i }
    }
    throw "No se encontró en el maestro: $What"
}

function Get-Block {
    param([string]$FromPattern, [string]$ToPattern)
    $start = Find-Line -Pattern $FromPattern
    $end   = Find-Line -Pattern $ToPattern -From ($start + 1)
    return $lines[$start..($end - 1)]
}

function Step-Down {
    # Baja un nivel los encabezados para que el extracto cuelgue de su título H1.
    param([string[]]$Content)
    $out = foreach ($line in $Content) {
        if ($line -match '^(#{1,5}) (.*)$') {
            ('#' * ($Matches[1].Length + 1)) + ' ' + $Matches[2]
        } else {
            $line
        }
    }
    # Recorta separadores y líneas vacías sobrantes al final.
    $out = @($out)
    while ($out.Count -gt 0 -and ($out[-1] -match '^\s*$' -or $out[-1] -match '^---+$')) {
        $out = $out[0..($out.Count - 2)]
    }
    return $out
}

function Write-VaultFile {
    param([string]$Path, [string[]]$Content)
    $dir = Split-Path -Parent $Path
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    [System.IO.File]::WriteAllText($Path, (($Content -join "`n") + "`n"), $utf8)
    Write-Host ("  generado  " + $Path.Replace($Vault + '\', ''))
}

$header = @(
    '> [!warning] Documento derivado (no editar a mano)',
    '> Generado desde [[00-Documento-Maestro-v1.3]] por `scripts/sync-vault-from-master.ps1`.',
    '> Cualquier corrección se hace en el maestro y se regenera: así no hay dos versiones de la verdad.',
    ''
)

Write-Host 'Sincronizando bóveda desde el maestro...'

# --- §2.1-§2.4 Modelo relacional -------------------------------------------------
$body = Step-Down (Get-Block '^## 2\.1 ' '^## 2\.5 ')
Write-VaultFile -Path (Join-Path $Vault '02-Arquitectura\Modelo-Relacional.md') -Content (@(
    '# Modelo Relacional & Diccionario de Datos', ''
    'Entidades, claves, catálogos y cardinalidades del SGT. El detalle de cálculos de inventario vive en [[03-Inventario]] y el de snapshots en [[06-Facturacion]].', ''
) + $header + $body)

# --- §2.9 Diagrama de clases UML ---------------------------------------------------
$body = Step-Down (Get-Block '^## 2\.9 ' '^# 3\. ')
$keep = @()
foreach ($line in $body) {
    if ($line -match '^#### Recursos editables') { break }
    $keep += $line
}
while ($keep.Count -gt 0 -and ($keep[-1] -match '^\s*$' -or $keep[-1] -match '^---+$')) {
    $keep = $keep[0..($keep.Count - 2)]
}
Write-VaultFile -Path (Join-Path $Vault '02-Arquitectura\Diagrama-Clases.md') -Content (@(
    '# Diagrama de Clases UML (dominio persistente)', ''
    'Clases del dominio derivadas de las 20 tablas del modelo lógico. Las operaciones de negocio viven en `service`, no en estas clases.', ''
) + $header + $keep + @(
    ''
    '> [!note] Exportaciones pendientes'
    '> Fuente editable: `02-Arquitectura/diagramas/CarMotors_Diagrama_Clases_UML_v1.3.mmd`. Los PNG/SVG se exportan desde ese archivo cuando se necesiten.'
))

# --- §5.1-§5.3 Requerimientos no funcionales -------------------------------------
$body = Step-Down (Get-Block '^## 5\.1 ' '^## 5\.4 ')
Write-VaultFile -Path (Join-Path $Vault '02-Arquitectura\Requerimientos-No-Funcionales.md') -Content (@(
    '# Requerimientos No Funcionales (RNF-01 a RNF-25)', ''
    'Arquitectura, capas, persistencia y condiciones de entrega. Son la base del [[Definition-of-Done]] y de la rúbrica.', ''
) + $header + $body)

# --- §5.6 Transacciones JDBC ------------------------------------------------------
$body = Step-Down (Get-Block '^## 5\.6 ' '^## 5\.7 ')
Write-VaultFile -Path (Join-Path $Vault '02-Arquitectura\Transacciones-JDBC.md') -Content (@(
    '# Transacciones JDBC & Concurrencia', ''
    'Regla de oro: una unidad de trabajo = una `Connection` = un `commit` o un `rollback`. Ver también [[Definition-of-Done]] y [[05-Servicios-Ordenes]].', ''
) + $header + $body)

# --- §8 Plan de iteraciones -------------------------------------------------------
$body = Step-Down (Get-Block '^# 8\. Plan' '^# 9\. ')
Write-VaultFile -Path (Join-Path $Vault '03-Gestion\Plan-Iteraciones.md') -Content (@(
    '# Plan de las 9 Ramas Feature & Dependencias', ''
    'Orden de construcción acordado en el maestro. Cada rama se abre desde `develop` y vuelve por Pull Request (ver [[Git-Flow]]).', ''
) + $header + $body)

# --- §6 Historias de usuario ------------------------------------------------------
$moduleMap = @{
    'Inventario'             = @{ Doc = '03-Inventario';          Rama = 'feature/gestion-inventario' }
    'Servicios y órdenes'    = @{ Doc = '05-Servicios-Ordenes';   Rama = 'feature/gestion-servicios' }
    'Clientes y vehículos'   = @{ Doc = '01-Clientes-Vehiculos';  Rama = 'feature/gestion-clientes' }
    'Facturación y reportes' = @{ Doc = '06-Facturacion';         Rama = 'feature/facturacion' }
    'Proveedores'            = @{ Doc = '02-Proveedores';         Rama = 'feature/gestion-proveedores' }
    'Técnicos'               = @{ Doc = '04-Tecnicos';            Rama = 'feature/gestion-tecnicos' }
}

# El maestro (§8) ubica HU-11 en la rama de reportes aunque su dominio sea clientes.
$ramaOverride = @{ 'HU-11' = 'feature/reportes'; 'HU-13' = 'feature/reportes'; 'HU-12' = 'feature/facturacion' }
$docOverride  = @{ 'HU-13' = '07-Reportes' }

$start = Find-Line '^# 6\. Historias de usuario'
$end   = Find-Line '^# 7\. ' -From ($start + 1)
$block = $lines[$start..($end - 1)]

$group   = $null
$current = $null
$counter = 0

function Save-Story {
    param($Story)
    $refs = $Story.Refs -split ',' | ForEach-Object { $_.Trim() } | Where-Object { $_ }
    $rf = @($refs | Where-Object { $_ -like 'RF-*' })
    $rn = @($refs | Where-Object { $_ -like 'RN-*' })

    $doc  = if ($docOverride.ContainsKey($Story.Id))  { $docOverride[$Story.Id] }  else { $moduleMap[$Story.Group].Doc }
    $rama = if ($ramaOverride.ContainsKey($Story.Id)) { $ramaOverride[$Story.Id] } else { $moduleMap[$Story.Group].Rama }

    $front = @(
        '---'
        "id: $($Story.Id)"
        "titulo: `"$($Story.Title)`""
        "modulo: `"$($Story.Group)`""
        "rama: $rama"
        'rf: [' + ($rf -join ', ') + ']'
        'rn: [' + ($rn -join ', ') + ']'
        'fuente: 00-Documento-Maestro-v1.3.md#6'
        '---'
        ''
    )

    $trace = @(
        ('# ' + $Story.Id + ' — ' + $Story.Title)
        ''
        '> [!info] Trazabilidad'
        ("> Módulo: [[" + $doc + "]] · Rama: ``" + $rama + "`` · Requerimientos: " + $(if ($rf) { $rf -join ', ' } else { '—' }) + $(if ($rn) { ' · Reglas: ' + ($rn -join ', ') } else { '' }))
        '> Fuente: [[00-Documento-Maestro-v1.3]] §6 · Criterios de aceptación en formato Dado / Cuando / Entonces.'
        ''
    ) + $Story.Body

    Write-VaultFile -Path (Join-Path $Vault ("04-Historias\" + $Story.Id + '.md')) -Content ($front + $trace)
}

foreach ($line in $block) {
    if ($line -match '^## (.+)$' -and $line -notmatch '^### ') {
        if ($current) { Save-Story $current; $current = $null }
        $group = $Matches[1].Trim()
        continue
    }
    if ($line -match '^###\s+(HU-\d+)\s+—\s+(.+?)\s+\(([^)]*)\)\s*$') {
        if ($current) { Save-Story $current }
        $counter++
        $current = [pscustomobject]@{
            Id    = $Matches[1]
            Title = $Matches[2].Trim()
            Refs  = $Matches[3]
            Group = $group
            Body  = @()
        }
        continue
    }
    if ($current) {
        if ($line -match '^---\s*$') { continue }
        $current.Body += $line
    }
}
if ($current) { Save-Story $current }

Write-Host ("Listo: {0} historias de usuario y 6 documentos derivados." -f $counter)
