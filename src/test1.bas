PRINT("FizzBuzz START")

num3 = 1.0
num5 = 1.0

FOR i=1 TO 100

    IF (i / 3.0) = num3 THEN
        IF (i / 5.0) = num5 THEN
            PRINT (i + " Fizz Buzz")
            num3 = num3 + 1.0
            num5 = num5 + 1.0
        ELSE
            PRINT (i + " Fizz")
            num3 = num3 + 1.0
        ENDIF

    ELSEIF (i / 5.0) = num5 THEN
        PRINT (i + " Buzz")
        num5 = num5 + 1.0

    ELSE
        PRINT i
    ENDIF

NEXT i

END