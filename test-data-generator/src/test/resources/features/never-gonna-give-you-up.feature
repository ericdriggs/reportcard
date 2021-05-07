@DataGenerate
@GiveYouUp
Feature:Never gonna give you up

  Scenario: You know the rules and so do I
    * match true == Coin.flip(1)

  Scenario:  give you up
    * match true == Coin.flip(.1)

  Scenario:  let you down
    * match true == Coin.flip(.2)

  Scenario:  run around and desert you
    * match true == Coin.flip(0)

  Scenario:  make you cry
    * match true == Coin.flip(.5)

  Scenario:  say goodbye
    * match true == Coin.flip(.6)

  Scenario:  tell a lie and hurt you
    * match true == Coin.flip(.3)

  Scenario: I'd run right into hell and back
    * match true == Coin.flip(.1)


