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
        console.error('Formulario profileEditorForm no encontrado');
    } else {
        form.addEventListener('submit', () => {
            try {
                const bioHtml = quill.root.innerHTML;
                const bioInput = document.getElementById('bioInput');
                if (bioInput) {
                    bioInput.value = bioHtml;
                } else {
                    console.error('Input bioInput no encontrado');
                }
            } catch (error) {
                console.error('Error al procesar el formulario:', error);
            }
        });
    }
} catch (error) {
    console.error('Error al inicializar Quill.js:', error);
}