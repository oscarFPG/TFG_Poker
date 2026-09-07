from .replay_buffer import ReplayBuffer
from .samples import StrategySample


class StrategyMemory(ReplayBuffer[StrategySample]):
    """
    Buffer de entrenamiento para la Strategy Network.
    """

    def add_sample(self, state, strategy, iteration,):

        sample = StrategySample(
            state=state,
            strategy=strategy,
            iteration=iteration,
        )

        self.add(sample)