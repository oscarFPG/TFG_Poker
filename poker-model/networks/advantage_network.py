from .base_network import BaseNetwork


class AdvantageNetwork(BaseNetwork):
    """
    Predice los regrets (counterfactual advantages).

    La salida NO lleva Softmax.
    Puede contener valores positivos y negativos.
    """

    def __init__(self, input_size=54, output_size=5, hidden_layers=(256, 256, 128), ):
        super().__init__(
            input_size=input_size,
            output_size=output_size,
            hidden_layers=hidden_layers,
        )