$docx = 'C:\admin-proyectos\docs\ProyectoIntegrador\Proyecto_Integrador_DevOps_IaC_AdminProyectos.docx'
$pdf  = 'C:\admin-proyectos\docs\ProyectoIntegrador\Proyecto_Integrador_DevOps_IaC_AdminProyectos.pdf'

Write-Host "Convirtiendo DOCX a PDF..."
try {
  $word = New-Object -ComObject Word.Application
  $word.Visible = $false
  $word.DisplayAlerts = [Microsoft.Office.Interop.Word.WdAlertLevel]::wdAlertsNone
  $doc = $word.Documents.Open($docx, $false, $true)
  $doc.SaveAs2($pdf, 17)
  $doc.Close()
  $word.Quit()
  [System.Runtime.InteropServices.Marshal]::ReleaseComObject($word) | Out-Null
  Write-Host "PDF GENERADO CON EXITO: $pdf"
} catch {
  Write-Error "Error en conversion COM: $_"
}
