from .replay_buffer import ReplayBuffer
from .samples import AdvantageSample


class AdvantageMemory(ReplayBuffer[AdvantageSample]):
    """
    Buffer de entrenamiento para la Advantage Network.
    """

    def add_sample(self, state, regrets, iteration,):

        sample = AdvantageSample(
            state=state,
            regrets=regrets,
            iteration=iteration,
        )

        self.add(sample)