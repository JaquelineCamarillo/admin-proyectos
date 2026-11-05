$docxPath = 'C:\admin-proyectos\docs\Practica3\P3_Reporte_Completo_AdminProyectos.docx'
$pdfPath  = 'C:\admin-proyectos\docs\Practica3\P3_Reporte_Completo_AdminProyectos.pdf'

if (-not (Test-Path $docxPath)) {
  Write-Error "DOCX not found: $docxPath"
  exit 1
}

Write-Host "Converting $docxPath to $pdfPath using Word COM..."
try {
  $word = New-Object -ComObject Word.Application
  $word.Visible = $false
  $word.DisplayAlerts = [Microsoft.Office.Interop.Word.WdAlertLevel]::wdAlertsNone

  $doc = $word.Documents.Open($docxPath, $false, $true)
  # 17 = wdFormatPDF
  $doc.SaveAs2($pdfPath, 17)
  $doc.Close()
  $word.Quit()
  [System.Runtime.Interopservices.Marshal]::ReleaseComObject($word) | Out-Null
  Write-Host "PDF CREATED SUCCESSFULLY: $pdfPath"
} catch {
  Write-Error "Word COM conversion failed: $_"
  exit 1
}
