Write-Host "SETUP MODELO POKER (Qwen + Ollama)"
Write-Host "--------------------------------------"

#Comprobar Ollama
try {
    $version = ollama --version
    Write-Host "Ollama detectado: $version"
}
catch {
    Write-Host "❌ Ollama no está instalado o no está en PATH"
    Write-Host "👉 Instálalo desde: https://ollama.com"
    exit
}

#Crear carpeta modelo
$modelDir = "model-qwen"
if (!(Test-Path $modelDir)) {
    New-Item -ItemType Directory -Path $modelDir | Out-Null
}

Set-Location $modelDir

#Nombre del archivo
$modelFile = "qwen3-4b-pokerbench-grpo-q4_k_m.gguf"

#Descargar modelo si no existe
if (!(Test-Path $modelFile)) {
    Write-Host "Descargando modelo (~2.5GB, puede tardar)..."

    Invoke-WebRequest `
        -Uri "https://huggingface.co/YiPz/qwen3-4b-pokerbench-grpo-gguf/resolve/main/qwen3-4b-pokerbench-grpo-q4_k_m.gguf" `
        -OutFile $modelFile
}
else {
    Write-Host "Modelo ya descargado"
}

#Crear Modelfile
Write-Host "Creando Modelfile..."

@"
FROM ./qwen3-4b-pokerbench-grpo-q4_k_m.gguf

PARAMETER temperature 0.6
PARAMETER top_p 0.95
PARAMETER num_ctx 3072
PARAMETER stop "<|im_end|>"
PARAMETER stop "<|endoftext|>"

SYSTEM "You are an expert poker coach. Analyze situations with step-by-step reasoning in <think></think> tags and provide your action in <action></action> tags."

TEMPLATE """{{- if .System }}<|im_start|>system
{{ .System }}<|im_end|>
{{ end }}{{- range .Messages }}<|im_start|>{{ .Role }}
{{ .Content }}<|im_end|>
{{ end }}<|im_start|>assistant
"""
"@ | Out-File -Encoding utf8 Modelfile

#Crear modelo en Ollama
Write-Host "Registrando modelo en Ollama..."

ollama create qwenPokerBot -f Modelfile

#Volver a raíz
Set-Location ..


Write-Host "SETUP COMPLETADO"
Write-Host "Modelo disponible: qwenPokerBot"
Write-Host "-----------------------------------------------"