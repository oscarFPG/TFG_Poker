import numpy as np
import torch


class TrajectoryCollector:

    def __init__(self, env, adv_memory, strat_memory, policy_net):
        self.env = env
        self.adv_memory = adv_memory
        self.strat_memory = strat_memory
        self.policy_net = policy_net

    def play_episode(self, iteration):

        state, player_id = self.env.reset()
        trajectory = []

        done = False
        while not done:

            obs = state["obs"]
            legal_actions = list(state["legal_actions"].keys())

            # Policy network inference
            with torch.no_grad():

                x = torch.tensor(obs, dtype=torch.float32).unsqueeze(0)
                logits = self.policy_net(x).numpy()[0]

            # Mask illegal actions
            logits = np.array(logits)
            mask = np.full_like(logits, -1e9)
            mask[legal_actions] = logits[legal_actions]

            # Softmax
            exp_logits = np.exp(mask - np.max(mask))
            probs = exp_logits / np.sum(exp_logits)

            # Check
            probs = np.nan_to_num(probs)
            probs = np.clip(probs, 0, 1)
            probs = probs / np.sum(probs)

            action = np.random.choice(len(probs), p=probs)
            trajectory.append({
                "obs": obs,
                "action": action,
                "probs": probs,
                "player": player_id
            })

            state, player_id = self.env.step(action)
            done = self.env.is_over()

        # Compute final reward
        payoffs = self.env.get_payoffs()

        # Store training samples
        for t in trajectory:

            obs = t["obs"]
            action = t["action"]
            probs = t["probs"]
            player = t["player"]
            reward = np.tanh(payoffs[player] / 50.0)

            # Advantage (aproximado MCCFR)
            baseline = np.mean(probs) * reward
            regret = np.zeros(self.env.num_actions)
            regret[action] = reward - baseline

            self.adv_memory.add_sample(
                obs,
                regret,
                iteration
            )

            # Strategy
            self.strat_memory.add_sample(
                obs,
                probs,
                iteration
            )