class DeepCFRAgent:

    def __init__(self, env, collector, trainer):
        self.env = env
        self.collector = collector
        self.trainer = trainer

    def train(self, num_iterations=1000):

        for it in range(num_iterations):

            self.collector.play_episode(it)

            adv_loss = self.trainer.train_advantage()
            strat_loss = self.trainer.train_strategy()

            if it % 10 == 0:
                print(
                    f"Iter {it} | "
                    f"AdvLoss: {adv_loss} | "
                    f"StratLoss: {strat_loss}"
                )