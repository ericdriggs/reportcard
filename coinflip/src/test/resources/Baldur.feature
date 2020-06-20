@DataGenerate
Feature:What does Baldur fear?

  Scenario Outline: Mistletoe

    * match true == Coin.flip(ratio)
    Examples:
      | ratio |
      | .1    |
      | .3    |
      | .4    |
      | .8    |
      | .9124 |
