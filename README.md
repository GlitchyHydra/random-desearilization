# random-desearilization
Random generator by deserealization in Kotlinx-Serialization library
It's a random decoder, that you can give to deserialize method of your class serializer. It will generate object with random values in it. 
Also you can generate random values in specific range by setting a annotation to your fields in class.
Generator will give you a specific values in common.
empty collections;
-1, 0, 1 in Int, Short, Long; 
Nan, infinity in Double, Float;
And english alphabet chars.
