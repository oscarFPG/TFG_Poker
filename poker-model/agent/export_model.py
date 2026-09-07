import torch

def export_model(model, input_size, path="model.onnx"):

    dummy_input = torch.randn(1, input_size)

    torch.onnx.export(
        model,
        dummy_input,
        "poker_cfr.onnx",
        dynamo=False,
        export_params=True,
        opset_version=11
    )

    print(f"Model exported to {path}")