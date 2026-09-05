from abc import ABC
from random import sample
from typing import Generic, List, TypeVar

T = TypeVar("T")


class ReplayBuffer(Generic[T], ABC):
    """
    Buffer genérico para almacenar muestras.
    """

    def __init__(self):
        self._memory: List[T] = []

    def __len__(self):
        return len(self._memory)

    def clear(self):
        self._memory.clear()

    def add(self, item: T):
        self._memory.append(item)

    def sample(self, batch_size: int) -> List[T]:

        if batch_size > len(self._memory):
            raise ValueError(
                "Batch size larger than memory size."
            )

        return sample(self._memory, batch_size)

    @property
    def memory(self):
        return self._memory