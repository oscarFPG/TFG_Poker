import numpy as np


def regret_matching(regrets: np.ndarray) -> np.ndarray:
    """
    Convierte regrets en una política.

    π(a) ∝ max(regret(a), 0)
    """

    positive_regrets = np.maximum(regrets, 0)

    normalizer = np.sum(positive_regrets)

    if normalizer > 0:
        return positive_regrets / normalizer

    # fallback: uniforme
    return np.ones_like(regrets) / len(regrets)