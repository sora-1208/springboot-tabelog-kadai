const editButton = document.querySelector('#editButton');
 
editButton.addEventListener('click', () => {
	let content = document.getElementById('editable');
	content.contentEditable = 'true';
	content.style.background = '#ffcccc';
	console.log(content);
});