import numpy as np
import torch
import rlcard
import evaluate


class RandomAgent:

    def act(self, state):
        legal = list(state["legal_actions"].keys())
        return np.random.choice(legal)


class ModelAgent:

    def __init__(self, policy_net, num_actions):
        self.policy_net = policy_net
        self.num_actions = num_actions

    def act(self, state):

        obs = state["obs"]

        legal = list(state["legal_actions"].keys())

        with torch.no_grad():

            x = torch.tensor(obs, dtype=torch.float32).unsqueeze(0)

            logits = self.policy_net(x).numpy()[0]

        # mask ilegal
        mask = np.full_like(logits, -1e9)
        mask[legal] = logits[legal]

        probs = np.exp(mask - np.max(mask))
        probs = probs / np.sum(probs)

        return np.random.choice(len(probs), p=probs)