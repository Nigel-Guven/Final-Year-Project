def remove_html_markup(s):		#https://stackoverflow.com/questions/9662346/python-code-to-remove-html-tags-from-a-string
	tag = False
	quote = False
	out = ""

	for c in s:
		if c == '<' and not quote:
			tag = True
		elif c == '>' and not quote:
			tag = False
		elif (c == '"' or c == "'") and tag:
			quote = not quote
		elif not tag:
			out = out + c

	return out
