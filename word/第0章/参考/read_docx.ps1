$word = New-Object -ComObject Word.Application
$word.Visible = $false
$doc = $word.Documents.Open('D:\project\java\imooc-wiki\word\第0章\参考\北林学位格式（2023版修订）.docx')

$text = $doc.Content.Text

# 输出到文件
$text | Out-File -FilePath 'D:\project\java\imooc-wiki\word\第0章\参考\format.txt' -Encoding UTF8

# 显示前8000字符
$len = [Math]::Min(8000, $text.Length)
Write-Host $text.Substring(0, $len)

$doc.Close()
$word.Quit()
