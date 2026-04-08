Write-Host "SETUP MODELO POKER (LLAMA + Ollama)"
Write-Host "--------------------------------------"

# Comprobar Ollama
try {
    $version = ollama --version
    Write-Host "Ollama detectado: $version"
}
catch {
    Write-Host "Ollama no está instalado o no está en PATH"
    Write-Host "Instálalo desde: https://ollama.com"
    exit
}

# Crear carpeta modelo
$modelDir = "model-llama"
if (!(Test-Path $modelDir)) {
    New-Item -ItemType Directory -Path $modelDir | Out-Null
}

Set-Location $modelDir

# Nombre del archivo
$modelFile = "llama3-8b-pokerbench-sft-q4_k_m.gguf"

# Descargar modelo si no existe
if (!(Test-Path $modelFile)) {
    Write-Host "Descargando modelo (~4-5GB, puede tardar)..."

        Invoke-WebRequest `
        -Uri "https://huggingface.co/YiPz/llama3-8b-pokerbench-sft-gguf/resolve/main/llama3-8b-pokerbench-sft-q4_k_m.gguf" `
        -OutFile $modelFile
}
else {
    Write-Host "Modelo ya descargado"
}

#Crear Modelfile
Write-Host "Creando Modelfile..."

@"
FROM ./llama3-8b-pokerbench-sft-q4_k_m.gguf
PARAMETER temperature 0.1
SYSTEM "You are an expert poker player. Respond with your action in <action>answer</action> tags."
"@ | Out-File -Encoding utf8 Modelfile

# Crear modelo en Ollama
Write-Host "Registrando modelo en Ollama..."

ollama create llamaPokerBot -f Modelfile

# Volver a raíz
Set-Location ..

Write-Host "SETUP COMPLETADO (LLAMA)"
Write-Host "-----------------------------------------------"