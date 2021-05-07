@DataGenerate
@GiveYouUp
Feature:Never gonna give you up

  Scenario: You know the rules and so do I
    * match true == BooleanGenerator.generateBoolean(.8)

  Scenario:  give you up
    * match true == BooleanGenerator.generateBoolean(.1)

  Scenario:  let you down
    * match true == BooleanGenerator.generateBoolean(.2)

  Scenario:  run around and desert you
    * match true == BooleanGenerator.generateBoolean(0)

  Scenario:  make you cry
    * match true == BooleanGenerator.generateBoolean(.5)

  Scenario:  say goodbye
    * match true == BooleanGenerator.generateBoolean(.6)

  Scenario:  tell a lie and hurt you
    * match true == BooleanGenerator.generateBoolean(.3)

