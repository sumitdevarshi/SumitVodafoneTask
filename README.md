# SumitVodafoneTask
VodafoneTask
How to test this project -

end-to-end test from postman 

post request:-  

http://localhost:9091/iot/v1/event

body -

{
	filepath: "E:/path/to/data.csv"
}

make sure file is availabe before test

get request:-

http://localhost:9091/iot/v1/event?ProductId=WG11155638?tstmp=1580000000000

