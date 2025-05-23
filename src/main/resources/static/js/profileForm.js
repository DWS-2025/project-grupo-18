try {
    const quill = new Quill('#quill-editor', {
        theme: 'snow',
        modules: {
            toolbar: [
                ['bold', 'italic', 'underline'],
                ['link'],
                [{ 'list': 'ordered' }, { 'list': 'bullet' }]
            ]
        }
    });

    const form = document.getElementById('profileEditorForm');
    if (!form) {
        console.error('profileEditorForm not found');
    } else {
        form.addEventListener('submit', () => {
            try {
                const bioHtml = quill.root.innerHTML;
                const bioInput = document.getElementById('bioInput');
                if (bioInput) {
                    bioInput.value = bioHtml;
                } else {
                    console.error('Input bioInput not found');
                }
            } catch (error) {
                console.error('Error processing the form:', error);
            }
        });
    }
} catch (error) {
    console.error('Error inicializating Quill.js:', error);
}
