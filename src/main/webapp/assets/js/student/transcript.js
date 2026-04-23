function generatePDF(firstName, lastName) {
    const element = document.getElementById('transcriptContent');
    const opt = {
        margin:       10,
        filename:     `Transcript_${firstName}_${lastName}.pdf`,
        image:        { type: 'jpeg', quality: 0.98 },
        html2canvas:  { scale: 2 },
        jsPDF:        { unit: 'mm', format: 'a4', orientation: 'portrait' }
    };

    // Output to PDF
    html2pdf().set(opt).from(element).save();
}
