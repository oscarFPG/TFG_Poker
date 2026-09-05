import rlcard

env = rlcard.make("no-limit-holdem")
state, player_id = env.reset()

# Players info
print("=" * 60)
print("Current player ID:", player_id)


# States info
print(f"\nAll states keys: {state.keys()}")
print(f"Full state: {state}")
print("=" * 60)
print()

## Raw observations
spacing = "=" * 30;
final_text = spacing + " Raw obs " + spacing
print(final_text)
print(f"Raw obs: {state["raw_obs"]}")
print("=" * len(final_text))
print()

## Observations
final_text = spacing + " Obs " + spacing
print(final_text)
print(f"Obs: {state["obs"]}")
print(f"Obs type: {type(state["obs"])}")
print(f"Obs length: {len(state["obs"])}")
print("=" * len(final_text))
print()

#print(f"\nLegal actions: {state["legal_actions"]}")
#print("=" * 60)
#print()

# Simulation
done = False
while not done:

    state = env.get_state(env.get_player_id())
    action = list(state["legal_actions"].keys())[0]
    print("Acción elegida:", action)

    next_state, next_player = env.step(action)
    done = env.is_over()