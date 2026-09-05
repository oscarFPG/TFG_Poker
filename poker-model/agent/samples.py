from dataclasses import dataclass

import numpy as np


@dataclass(slots=True)
class AdvantageSample:
    """
    Muestra utilizada para entrenar la Advantage Network.
    """

    state: np.ndarray
    regrets: np.ndarray
    iteration: int


@dataclass(slots=True)
class StrategySample:
    """
    Muestra utilizada para entrenar la Strategy Network.
    """

    state: np.ndarray
    strategy: np.ndarray
    iteration: int