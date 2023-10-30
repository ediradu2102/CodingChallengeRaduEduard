# CodingChallengeRaduEduard
Web application that allows users to query earthquake data for a specific country and a defined time frame. 

I created both a back end and a front end for this project, to use the program, users have to run the main class: RaduEduardAdessoCodingChallengeApplication, and then type the following URL in the browser: 
http://localhost:8081/home
Users can then use the fields there to obtain information about recent earthquakes in a desired country

The information can also be accessed in Postman using any of the created endpoints:
Postman example:
GET: http://localhost:8081/earthquakes/byCountry?country=Turkey&pastDays=5

Or by typing:
http://localhost:8081/earthquakes/byCountry
then going to Query Params and entering: country and pastdays as keys and Tukey and 5 as value

If you are looking for countries with a lot of earthquakes try Canada or Brazil :) 

For example Canada in the past 5 days, however, the information updates from day to day.
