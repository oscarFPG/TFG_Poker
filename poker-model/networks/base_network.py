import torch
import torch.nn as nn


class BaseNetwork(nn.Module):
    """
    Red neuronal base utilizada tanto por la Advantage Network
    como por la Strategy Network.

    Arquitectura:

        Input (54)
            |
        Linear(54,256)
            |
        ReLU
            |
        Linear(256,256)
            |
        ReLU
            |
        Linear(256,128)
            |
        ReLU
            |
        Linear(128,5)
    """

    def __init__(self, input_size: int = 54, output_size: int = 5, hidden_layers=(256, 256, 128),):
        super().__init__()

        layers = []
        previous = input_size

        for hidden in hidden_layers:
            layers.append(nn.Linear(previous, hidden))
            layers.append(nn.ReLU())
            previous = hidden

        layers.append(nn.Linear(previous, output_size))
        self.network = nn.Sequential(*layers)

    def forward(self, x):
        
        if x.dim() == 1:
            x = x.unsqueeze(0)

        return self.network(x)