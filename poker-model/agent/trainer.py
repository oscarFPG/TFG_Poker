import torch
import torch.nn as nn
from torch.utils.data import DataLoader, TensorDataset


class Trainer:

    def __init__(self, adv_net, strat_net, adv_memory, strat_memory):

        self.adv_net = adv_net
        self.strat_net = strat_net
        self.adv_memory = adv_memory
        self.strat_memory = strat_memory

        self.optim_adv = torch.optim.Adam(
            self.adv_net.parameters(), lr=1e-3
        )

        self.optim_strat = torch.optim.Adam(
            self.strat_net.parameters(), lr=1e-3
        )

        self.adv_loss_fn = nn.MSELoss()
        self.strat_loss_fn = nn.KLDivLoss(reduction="batchmean")

    def train_advantage(self, batch_size=256):

        if len(self.adv_memory) < batch_size:
            return

        batch = self.adv_memory.sample(batch_size)
        states = torch.tensor([b.state for b in batch], dtype=torch.float32)
        regrets = torch.tensor([b.regrets for b in batch], dtype=torch.float32)

        pred = self.adv_net(states)
        loss = self.adv_loss_fn(pred, regrets)

        self.optim_adv.zero_grad()
        loss.backward()
        self.optim_adv.step()

        return loss.item()

    def train_strategy(self, batch_size=256):

        if len(self.strat_memory) < batch_size:
            return

        batch = self.strat_memory.sample(batch_size)
        states = torch.tensor([b.state for b in batch], dtype=torch.float32)
        target = torch.tensor([b.strategy for b in batch], dtype=torch.float32)

        logits = self.strat_net(states)
        log_probs = torch.log_softmax(logits, dim=-1)
        loss = self.strat_loss_fn(log_probs, target)

        self.optim_strat.zero_grad()
        loss.backward()
        self.optim_strat.step()

        return loss.item()