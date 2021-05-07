@DataGenerate
@Fox
Feature:What does the fox say?

  Scenario Outline: Range check
    * match true == BooleanGenerator.generateBoolean(ratio)
    Examples:
      | ratio |
      | -5    |
      | 0     |
      | .5    |
      | 1     |
      | 3     |

  Scenario: Ring-ding-ding-ding-dingeringeding!
    * match true == BooleanGenerator.generateBoolean(.6)

  Scenario: Wa-pa-pa-pa-pa-pa-pow!
    * match true == BooleanGenerator.generateBoolean(.7)

  Scenario: Hatee-hatee-hatee-ho!
    * match true == BooleanGenerator.generateBoolean(.8)