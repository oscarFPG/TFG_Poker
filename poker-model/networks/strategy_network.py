import torch

from .base_network import BaseNetwork


class StrategyNetwork(BaseNetwork):
    """
    Red que aproxima la estrategia promedio.

    Devuelve logits.

    Las probabilidades se obtienen aplicando Softmax únicamente
    cuando sea necesario.
    """

    def __init__(self, input_size=54, output_size=5, hidden_layers=(256, 256, 128), ):
        super().__init__(
            input_size=input_size,
            output_size=output_size,
            hidden_layers=hidden_layers,
        )

    @torch.no_grad()
    def predict_policy(self, state):
        
        logits = self.forward(state)
        return torch.softmax(logits, dim=-1)