# github-testing

1. Clone the project -
  ` $ git clone https://github.com/JeniaTaranov/github-testing.git`
   
2. Install gradle on your OS, the project compilable with jdk17.
   
3. Create a `src/test/resources/local.properties` file and store inside it two fields: 
   access-token and owner-name. 
   The owner-name value will be “JeniaTaranov”. 
   Add the access-token that have been provided to you by the project owner.
   
4. To run the project via CLI, run the following commands from the project directory:
   `$ gradle clean `
   and then 
   `$ gradle build` or 
   `$ grade test`
   
5. Another option to run the project is via the IDE you are using, intellij suggested for Java. 
