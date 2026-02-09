**Archtecture and why ?**
After a lot of research for finding a good architecture, I come to use Modular monolith, 
here's why
- maintaining code
- easy testing
- better scaling for teams
- better scaling than tradition monolith
- easy to convert to microservice in future
- easy to integrate DDD

For implementing DDD with modular monolith, I have paired this with Clean Architecture.
Reason being, Clean Architecture is easy to understand as compared to Hexagonal, it has less boilerplate
at least for me, I feel it's better.

here's video on clean architecture: https://www.youtube.com/watch?v=TQdLgzVk2T8
For implementation of this architecture, I am using spring-modulith, 
