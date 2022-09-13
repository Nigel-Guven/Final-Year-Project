from bs4 import BeautifulSoup	#https://www.crummy.com/software/BeautifulSoup/
from requests import get
import scriptfunctions

from classes import Vehicle_Object

def main():			#https://www.motorcheck.ie/free-car-check/?vrm= DATA TAKEN FROM THIS SITE


	motorcheck_url = 'https://www.motorcheck.ie/free-car-check/?vrm=08D8764'
	response_from_motorcheck_url = get(motorcheck_url)
	url_soup = BeautifulSoup(response_from_motorcheck_url.text, 'html.parser')
	page_data = str(url_soup.find_all('div', class_="col-md-6 align-items-center"))
	refined_data = scriptfunctions.remove_html_markup(page_data)

	print(refined_data )

if __name__=="__main__":
	main()
