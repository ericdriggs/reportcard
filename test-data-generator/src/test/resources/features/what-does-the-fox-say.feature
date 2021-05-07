@DataGenerate
@Fox
Feature:What does the fox say?

  Scenario Outline: Range check
    * match true == Coin.flip(ratio)
    Examples:
      | ratio |
      | -5    |
      | 0     |
      | .5    |
      | 1     |
      | 3     |

  Scenario: Ring-ding-ding-ding-dingeringeding!
    * match true == Coin.flip(.6)

  Scenario: Wa-pa-pa-pa-pa-pa-pow!
    * match true == Coin.flip(.7)

  Scenario: Hatee-hatee-hatee-ho!
    * match true == Coin.flip(.8)