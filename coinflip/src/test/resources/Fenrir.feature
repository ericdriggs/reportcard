@DataGenerate
Feature:What does the fox say?

  Scenario Outline: Ring-ding-ding-ding-dingeringeding

    * match true == Coin.flip(ratio)
    Examples:
      | ratio |
      | -5    |
      | 0     |
      | .5    |
      | 1     |
      | 3     |