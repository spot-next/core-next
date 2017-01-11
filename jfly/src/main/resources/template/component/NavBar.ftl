<nav class="navbar ${navbarStyle}">
	<div class="container-fluid">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" 
				data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
				
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			
			${header.build().render()}
		</div>

		<div class="collapse navbar-collapse">
			<ul class="nav navbar-nav">
				<#list ${children} as child>
					<li>${child.build().render()}</li>
				</#list>
			</ul>
		</div>
	</div>
</nav>