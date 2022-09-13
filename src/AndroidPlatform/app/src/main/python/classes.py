class Vehicle_Object:

	def __init__(self, car_type, car_registration, car_year, car_color, car_engine_cc, car_fuel_type, car_body):		
		self.car_type = car_type
		self.car_registration = car_registration
		self.car_year = car_year
		self.car_color = car_color
		self.car_engine_cc = car_engine_cc
		self.car_fuel_type = car_fuel_type
		self.car_body = car_body
	
	def __repr__(self):
		return str(self)

class User_Object:
	
	def __init__(self, address, date_of_birth, isdriver, email, name, gender, mobile, profile_photo_url):				
		self.address = address
		self.date_of_birth = date_of_birth
		self.isdriver = isdriver
		self.email = email
		self.name = name
		self.gender = gender
		self.mobile = mobile
		self.profile_photo_url = profile_photo_url
			
	def __repr__(self):
		return str(self)

class Contact_Object:

	def __init__(self, driver, email, mobile_no, profile_photo_url, surname, uid):		
		self.driver = driver
		self.email = email
		self.mobile_no = mobile_no
		self.profile_photo_url = profile_photo_url
		self.surname = surname
		self.uid = uid
		
	def __repr__(self):
		return str(self)

class Message_Object:

	def __init__(self, author, message_contents, message_type, message_receiver, messageID):		
		self.author = author
		self.message_contents = message_contents
		self.message_type = message_type
		self.message_receiver = message_receiver
		self.messageID = messageID
			
	def __repr__(self):
		return str(self)

class Group_Object:

	def __init__(self, group_name, users, posts):	
		self.group_name = group_name
		self.users = users
		self.posts = posts
		
			
	def __repr__(self):
		return str(self)

class Device_Object:

	def __init__(self, myDeviceModel):		
		self.myDeviceModel = android.os.Build.MODEL
		
	def __repr__(self):
		return str(self)

class Driver_Object(User_Object):

	def __init__(self, vehicle):		
		self. vehicle =  vehicle

	def __repr__(self):
		return str(self)

class Route_Object:

	def __init__(self, route_start, route_end, route_duration, route_distance, start_coordinate, end_coordinate, date, time, name, email, mobile, is_offer, date_for_comp, user_id):		
		self.route_start = route_start
		self.route_end = route_end
		self.route_duration = route_duration
		self.route_distance = route_distance
		self.start_coordinate = start_coordinate
		self.end_coordinate = end_coordinate
		self.date = date
		self.time = time
		self.name = name
		self.email = email
		self.mobile = mobile
		self.is_offer = is_offer
		self.date_for_comp = date_for_comp
		self.user_id = user_id
			
	def __repr__(self):
		return str(self)

class SimpleDate_Object:

	def __init__(self, day, month, year):		
		self.day = day
		self.month = month
		self.year = year
		
	def __repr__(self):
		return str(self)
